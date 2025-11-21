package com.luohuo.basic.base.manager;

import org.springframework.lang.NonNull;
import com.luohuo.basic.model.cache.CacheKey;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * 基于MP的 IService 新增了几个方法： getByIdCache、getByKey、findByIds、refreshCache、clearCache
 * 其中：
 * 1，getByIdCache 方法 会先从缓存查询，后从DB查询 （取决于实现类）
 * 2、getByKey  根据提供的缓存key查询单个实体，查不到会通过loader回调查询
 * 3、findByIds 根据id批量从缓存查询实体列表
 * 4、refreshCache 刷新缓存
 * 5、clearCache 淘汰缓存
 *
 * @param <T> 实体
 * @author 乾乾
 * @date 2020年03月03日20:49:03
 */
public interface SuperCacheManager<T> extends SuperManager<T> {

    /**
     * 根据id 先查缓存，再查db
     *
     * @param id 主键
     * @return 对象
     */
    T getByIdCache(Serializable id);

    /**
     * 根据 key 查询缓存中存放的id，缓存不存在根据loader加载并写入数据，然后根据查询出来的id查询 实体
     *
     * @param key    缓存key
     * @param loader 加载器
     * @return 对象
     */
    T getByKey(CacheKey key, Function<CacheKey, Object> loader);

    /**
     * 根据ID，批量查询缓存。
     * 若缓存中不存在某条数据，则去数据库中加载数据， 数据库中不存在的数据，直接缓存空缓存。
     * <p>
     * 1. 分批次从redis通过 mget 命令获取数据
     * 2. 将redis中不存在的数据(missedIds)通过loader方法 查询出来
     * 3. 将loader方法查询出来的结果(missList) 设置到redis中缓存起来
     * 4. 将loader方法方法未查询出来的结果，设置到redis缓存为 "空值"
     * <p>
     * 注意：
     * 1. ids 参数的数量和返回值的数量一致
     * 2. 若数据库中不存在 ids 的某一个值，返回值对应的数据为 null， 并将会向redis缓存空值
     *
     * @param ids    主键id
     * @param loader 回调
     * @return 对象集合
     */
    List<T> findByIds(@NonNull Collection<? extends Serializable> ids, Function<Collection<? extends Serializable>, Collection<T>> loader);

    /**
     * 根据id 批量查询缓存
     *
     * @param keyIdList    id集合
     * @param cacheBuilder 缓存key构造器
     * @param loader       加载数据的回调方法
     * @param <E>          查询的对象
     * @return
     */
    <E> Set<E> findCollectByIds(List<Long> keyIdList, Function<Long, CacheKey> cacheBuilder, Function<Long, List<E>> loader);

    /**
     * 刷新缓存
     */
    void refreshCache(List<Long> ids);

    /**
     * 清理缓存
     */
    void clearCache(List<Long> ids);

    /**
     * 清理缓存
     *
     * @param ids
     */
    void delCache(Serializable... ids);

    /**
     * 清理缓存
     *
     * @param idList
     */
    void delCache(Collection<?> idList);

    /**
     * 清理缓存
     *
     * @param model
     */
    void delCache(T model);

    /**
     * 设置缓存
     *
     * @param model
     */
    void setCache(T model);
}
