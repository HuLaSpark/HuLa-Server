package com.luohuo.basic.base.service;

import org.springframework.lang.NonNull;
import com.luohuo.basic.base.entity.SuperEntity;
import com.luohuo.basic.model.cache.CacheKey;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

/**
 * 基于MP的 IService 新增了3个方法： getByIdCache
 * 其中：
 * 1，getByIdCache 方法 会先从缓存查询，后从DB查询 （取决于实现类）
 * 2、SuperService 上的方法
 *
 * @param <Id>     ID
 * @param <Entity> 实体
 * @author 乾乾
 * @date 2020年03月03日20:49:03
 */
public interface SuperCacheService<Id extends Serializable, Entity extends SuperEntity<?>>
        extends SuperService<Id, Entity> {

    /**
     * 根据id 先查缓存，再查db
     *
     * @param id 主键
     * @return 对象
     */
    Entity getByIdCache(Id id);

    /**
     * 根据 key 查询缓存中存放的id，缓存不存在根据loader加载并写入数据，然后根据查询出来的id查询 实体
     *
     * @param key    缓存key
     * @param loader 加载器
     * @return 对象
     */
    Entity getByKey(CacheKey key, Function<CacheKey, Object> loader);

    /**
     * 可能会缓存穿透
     *
     * @param ids    主键id
     * @param loader 回调
     * @return 对象集合
     */
    List<Entity> findByIds(@NonNull Collection<? extends Serializable> ids, Function<Collection<? extends Serializable>, Collection<Entity>> loader);

    /**
     * 刷新缓存
     *
     * @param ids 主键
     */
    void refreshCache(List<Long> ids);

    /**
     * 清理缓存
     *
     * @param ids 主键
     */
    void clearCache(List<Long> ids);
}
