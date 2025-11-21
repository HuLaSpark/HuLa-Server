package com.luohuo.basic.base.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;
import com.luohuo.basic.base.entity.SuperEntity;
import com.luohuo.basic.base.manager.SuperCacheManager;
import com.luohuo.basic.base.service.SuperCacheService;
import com.luohuo.basic.cache.repository.CacheOps;
import com.luohuo.basic.model.cache.CacheKey;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
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
 * @param <M>      Manager
 * @param <Id>     ID
 * @param <Entity> 实体
 * @author 乾乾
 * @date 2020年02月27日18:15:17
 */
public abstract class SuperCacheServiceImpl<M extends SuperCacheManager<Entity>, Id extends Serializable, Entity extends SuperEntity<?>>
        extends SuperServiceImpl<M, Id, Entity>
        implements SuperCacheService<Id, Entity> {

    @Autowired
    protected CacheOps cacheOps;

    @Override
    @Transactional(readOnly = true)
    public Entity getByIdCache(Id id) {
        return superManager.getByIdCache(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Entity> findByIds(@NonNull Collection<? extends Serializable> ids, Function<Collection<? extends Serializable>, Collection<Entity>> loader) {
        return superManager.findByIds(ids, loader);
    }

    @Override
    @Transactional(readOnly = true)
    public Entity getByKey(CacheKey key, Function<CacheKey, Object> loader) {
        return superManager.getByKey(key, loader);
    }

    @Override
    public void refreshCache(List<Long> ids) {
        superManager.refreshCache(ids);
    }

    @Override
    public void clearCache(List<Long> ids) {
        superManager.clearCache(ids);
    }

}
