package com.luohuo.basic.base.manager.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;
import com.luohuo.basic.base.entity.SuperEntity;
import com.luohuo.basic.base.manager.SuperCacheManager;
import com.luohuo.basic.base.mapper.SuperMapper;
import com.luohuo.basic.cache.redis2.CacheResult;
import com.luohuo.basic.cache.repository.CacheOps;
import com.luohuo.basic.database.mybatis.conditions.Wraps;
import com.luohuo.basic.model.cache.CacheKey;
import com.luohuo.basic.model.cache.CacheKeyBuilder;
import com.luohuo.basic.utils.CollHelper;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Function;

/**
 * 基于 CacheOps 实现的 缓存实现
 * 默认的key规则： #{CacheKeyBuilder#key()}:id
 * <p>
 * 1，getByIdCache：新增的方法： 先查缓存，在查db
 * 2，removeById：重写 ServiceImpl 类的方法，删除db后，淘汰缓存
 * 3，removeByIds：重写 ServiceImpl 类的方法，删除db后，淘汰缓存
 * 4，updateAllById： 新增的方法： 修改数据（所有字段）后，淘汰缓存
 * 5，updateById：重写 ServiceImpl 类的方法，修改db后，淘汰缓存
 *
 * @param <M>
 * @param <T>
 * @author 乾乾
 * @date 2020年02月27日18:15:17
 */
public abstract class SuperCacheManagerImpl<M extends SuperMapper<T>, T extends SuperEntity> extends SuperManagerImpl<M, T> implements SuperCacheManager<T> {

    protected static final int MAX_BATCH_KEY_SIZE = 500;
    @Autowired
    protected CacheOps cacheOps;

    /**
     * 缓存key 构造器
     *
     * @return 缓存key构造器
     */
    protected abstract CacheKeyBuilder cacheKeyBuilder();

    @Override
    @Transactional(readOnly = true)
    public T getByIdCache(Serializable id) {
        if (id == null) {
            return null;
        }
        CacheKey cacheKey = cacheKeyBuilder().key(id);
        CacheResult<T> result = cacheOps.get(cacheKey, k -> super.getById(id));
        return result.getValue();
    }


    @Override
    @Transactional(readOnly = true)
    public <E> Set<E> findCollectByIds(List<Long> keyIdList, Function<Long, CacheKey> cacheBuilder, Function<Long, List<E>> loader) {
        if (CollUtil.isEmpty(keyIdList)) {
            return Collections.emptySet();
        }
        List<CacheKey> cacheKeys = keyIdList.stream().map(cacheBuilder).toList();
        // 通过 mGet 方法批量查询缓存
        List<CacheResult<List<E>>> resultList = cacheOps.find(cacheKeys);

        if (resultList.size() != cacheKeys.size()) {
            log.warn("key和结果数量不一致，请排查原因!");
        }
        /*
         * 有可能缓存中不存在某些缓存，导致resultList中的部分元素是null
         */
        Set<E> resultIdSet = new HashSet<>();
        for (int i = 0; i < resultList.size(); i++) {
            CacheResult<List<E>> result = resultList.get(i);
            List<E> resultIdList = result.asList();
            if (result.isNull()) {
                Long keyId = keyIdList.get(i);

                List<E> idList = loader.apply(keyId);
                CacheKey cacheKey = cacheKeys.get(i);
                cacheOps.set(cacheKey, idList);
                resultIdList = idList;
            }
            resultIdSet.addAll(resultIdList);
        }
        return resultIdSet;
    }

    private List<CacheResult<T>> find(List<CacheKey> keys) {
        return cacheOps.find(keys);
    }

    @Override
    @Transactional(readOnly = true)
    public List<T> findByIds(@NonNull Collection<? extends Serializable> ids, Function<Collection<? extends Serializable>, Collection<T>> loader) {
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }

        // 拼接keys
        List<CacheKey> keys = ids.stream().map(cacheKeyBuilder()::key).toList();
        // 切割
        List<List<CacheKey>> partitionKeys = Lists.partition(keys, MAX_BATCH_KEY_SIZE);

        // 用切割后的 partitionKeys 分批去缓存查， 返回的是缓存中存在的数据
        List<CacheResult<T>> valueList = partitionKeys.stream().map(this::find).flatMap(Collection::stream).toList();

        // 所有的key
        List<Serializable> keysList = Lists.newArrayList(ids);
        log.debug(StrUtil.format("keySize={}, valueSize={}", keysList.size(), valueList.size()));

        // 缓存中不存在的key
        Set<Serializable> missedIds = Sets.newLinkedHashSet();

        Map<Serializable, T> allMap = new LinkedHashMap<>();
        for (int i = 0; i < valueList.size(); i++) {
            CacheResult<T> v = valueList.get(i);
            Serializable k = keysList.get(i);
            if (v == null || v.isNull()) {
                missedIds.add(k);
                // null 占位
                allMap.put(k, null);
            } else {
                allMap.put(k, v.getValue());
            }
        }

        // 加载miss 的数据，并设置到缓存
        if (CollUtil.isNotEmpty(missedIds)) {
            if (loader == null) {
                loader = missIds -> super.listByIds(missIds.stream().filter(Objects::nonNull).map(Convert::toLong).toList());
            }
            /*
             * 从数据库加载数据
             *
             * 数据库中确实不存在某条数据时， missedIds 和 missList的数量可能不一致
             */
            Collection<T> missList = loader.apply(missedIds);

            ImmutableMap<Serializable, T> missMap = CollHelper.uniqueIndex(missList, item -> (Serializable) item.getId(), item -> item);

            // 将数据库中查询出来的数据
            allMap.forEach((k, v) -> {
                if (missMap.containsKey(k)) {
                    allMap.put(k, missMap.get(k));
                }
            });

            // 将缓存中不存在的数据，缓存 "空值" 或 "实际值"
            for (Serializable missedKey : missedIds) {
                CacheKey key = cacheKeyBuilder().key(missedKey);
                // 存在就缓存 实际值
                // 不存在就缓存 空值，防止缓存击穿
                cacheOps.set(key, missMap.getOrDefault(missedKey, null));
            }
        }

        Collection<T> values = allMap.values();
        return Lists.newArrayList(values);
    }

    @Override
    @Transactional(readOnly = true)
    public T getByKey(CacheKey key, Function<CacheKey, Object> loader) {
        Object id = cacheOps.get(key, loader);
        return id == null ? null : getByIdCache(Convert.toLong(id));
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeById(Serializable id) {
        boolean bool = super.removeById(id);
        delCache(id);
        return bool;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeByIds(Collection<?> idList) {
        if (CollUtil.isEmpty(idList)) {
            return true;
        }
        boolean flag = super.removeByIds(idList);

        delCache(idList);
        return flag;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(T model) {
        boolean save = super.save(model);
        setCache(model);
        return save;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateAllById(T model) {
        boolean updateBool = super.updateAllById(model);
        delCache(model);
        return updateBool;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(T model) {
        boolean updateBool = super.updateById(model);
        delCache(model);
        return updateBool;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveBatch(Collection<T> entityList, int batchSize) {
        String sqlStatement = getSqlStatement(SqlMethod.INSERT_ONE);
        return executeBatch(entityList, batchSize, (sqlSession, entity) -> {
            sqlSession.insert(sqlStatement, entity);

            // 设置缓存
            setCache(entity);
        });
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveOrUpdateBatch(Collection<T> entityList, int batchSize) {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(getEntityClass());
        Assert.notNull(tableInfo, "error: can not execute. because can not find cache of TableInfo for entity!");
        String keyProperty = tableInfo.getKeyProperty();
        Assert.notEmpty(keyProperty, "error: can not execute. because can not find column for id from entity!");

        BiPredicate<SqlSession, T> predicate = (sqlSession, entity) -> {
            Object idVal = ReflectionKit.getFieldValue(entity, keyProperty);
            return StringUtils.checkValNull(idVal)
                    || CollectionUtils.isEmpty(sqlSession.selectList(getSqlStatement(SqlMethod.SELECT_BY_ID), entity));
        };

        BiConsumer<SqlSession, T> consumer = (sqlSession, entity) -> {
            MapperMethod.ParamMap<T> param = new MapperMethod.ParamMap<>();
            param.put(Constants.ENTITY, entity);
            sqlSession.update(getSqlStatement(SqlMethod.UPDATE_BY_ID), param);

            // 清理缓存
            delCache(entity);
        };

        String sqlStatement = SqlHelper.getSqlStatement(this.getMapperClass(), SqlMethod.INSERT_ONE);
        return SqlHelper.executeBatch(getEntityClass(), log, entityList, batchSize, (sqlSession, entity) -> {
            if (predicate.test(sqlSession, entity)) {
                sqlSession.insert(sqlStatement, entity);
                // 设置缓存
                setCache(entity);
            } else {
                consumer.accept(sqlSession, entity);
            }
        });


    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateBatchById(Collection<T> entityList, int batchSize) {
        String sqlStatement = getSqlStatement(SqlMethod.UPDATE_BY_ID);
        return executeBatch(entityList, batchSize, (sqlSession, entity) -> {
            MapperMethod.ParamMap<T> param = new MapperMethod.ParamMap<>();
            param.put(Constants.ENTITY, entity);
            sqlSession.update(sqlStatement, param);

            // 清理缓存
            delCache(entity);
        });
    }

    @Override
    public void refreshCache(List<Long> ids) {
        Wrapper<T> wrap = null;
        if (CollUtil.isNotEmpty(ids)) {
            wrap = Wraps.<T>lbQ().in(SuperEntity::getId, ids);
        }
        list(wrap).forEach(this::setCache);
    }

    @Override
    public void clearCache(List<Long> ids) {
        Wrapper<T> wrap = null;
        if (CollUtil.isNotEmpty(ids)) {
            wrap = Wraps.<T>lbQ().in(SuperEntity::getId, ids);
        }
        list(wrap).forEach(this::delCache);
    }


    @Override
    public void delCache(Serializable... ids) {
        delCache(Arrays.asList(ids));
    }

    @Override
    public void delCache(Collection<?> idList) {
        CacheKey[] keys = idList.stream().map(id -> cacheKeyBuilder().key(id)).toArray(CacheKey[]::new);
        cacheOps.del(keys);
    }

    @Override
    public void delCache(T model) {
        Object id = getId(model);
        if (id != null) {
            CacheKey key = cacheKeyBuilder().key(id);
            cacheOps.del(key);
        }
    }

    @Override
    public void setCache(T model) {
        Object id = getId(model);
        if (id != null) {
            CacheKey key = cacheKeyBuilder().key(id);
            cacheOps.set(key, model);
        }
    }

    protected Object getId(T model) {
        if (model != null) {
            return model.getId();
        } else {
            // 实体没有继承 Entity 和 SuperEntity
            TableInfo tableInfo = TableInfoHelper.getTableInfo(getEntityClass());
            if (tableInfo == null) {
                return null;
            }
            // 主键类型
            Class<?> keyType = tableInfo.getKeyType();
            if (keyType == null) {
                return null;
            }
            // id 字段名
            String keyProperty = tableInfo.getKeyProperty();

            // 反射得到 主键的值
            Field idField = ReflectUtil.getField(getEntityClass(), keyProperty);
            return ReflectUtil.getFieldValue(model, idField);
        }
    }

}
