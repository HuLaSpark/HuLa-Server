package com.luohuo.basic.cache.repository;

import org.springframework.lang.NonNull;
import com.luohuo.basic.cache.redis2.CacheResult;
import com.luohuo.basic.model.cache.CacheKey;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

/**
 * 缓存操作公共接口
 *
 * @author 乾乾
 * @date 2019/08/07
 */
public interface CacheOps {

    /**
     * 删除指定的key
     *
     * @param keys 多个key
     * @return 删除个数
     */
    Long del(@NonNull CacheKey... keys);

    /**
     * 删除指定的key
     *
     * @param keys 多个key
     * @return 删除个数
     */
    Long del(@NonNull Collection<CacheKey> keys);

    /**
     * 删除指定的key
     *
     * @param keys 多个key
     * @return 删除个数
     */
    Long del(@NonNull String... keys);

    /**
     * 判断指定的key 是否存在
     *
     * @param key key
     * @return 是否存在
     */
    Boolean exists(@NonNull CacheKey key);

    /**
     * 添加到带有 过期时间的  缓存
     *
     * @param key             redis主键
     * @param value           值
     * @param cacheNullValues 是否缓存null对象
     */
    void set(@NonNull CacheKey key, Object value, boolean... cacheNullValues);

    /**
     * 根据key获取对象
     *
     * @param key             redis主键
     * @param cacheNullValues 是否缓存null对象
     * @return 值 不存在时，返回null
     */
    <T> CacheResult<T> get(@NonNull CacheKey key, boolean... cacheNullValues);

    /**
     * 根据key获取对象
     *
     * @param key             redis主键
     * @param cacheNullValues 是否缓存null对象
     * @return 值 不存在时，返回null
     */
    <T> CacheResult<T> get(@NonNull String key, boolean... cacheNullValues);

    /**
     * 根据keys获取对象
     *
     * @param keys redis主键
     * @return 值 不存在时，返回空集合
     */
    <T> List<CacheResult<T>> find(@NonNull Collection<CacheKey> keys);

    /**
     * 根据key获取对象
     * 不存在时，调用function回调获取数据，并set进入，然后返回
     *
     * @param key             redis主键
     * @param loader          加载器
     * @param cacheNullValues 是否缓存null对象
     * @return 值
     */
    <T> CacheResult<T> get(@NonNull CacheKey key, Function<CacheKey, ? extends T> loader, boolean... cacheNullValues);

    /**
     * 清空所有存储的数据
     */
    void flushDb();

    /**
     * 为键 key 储存的数字值加上一。
     *
     * @param key 一定不能为 {@literal null}.
     * @return 返回键 key 在执行加一操作之后的值。
     */
    Long incr(@NonNull CacheKey key);

    /**
     * 获取key中存放的Long值
     *
     * @param key    一定不能为 {@literal null}.
     * @param loader 加载
     * @return key中存储的的数字
     */
    Long getCounter(@NonNull CacheKey key, Function<CacheKey, Long> loader);

    /**
     * 为键 key 储存的数字值加上increment。
     *
     * @param key       一定不能为 {@literal null}.
     * @param increment 增量值
     * @return 返回键 key 在执行加一操作之后的值。
     */
    Long incrBy(@NonNull CacheKey key, long increment);

    /**
     * 为键 key 储存的数字值加上一。
     *
     * @param key       一定不能为 {@literal null}.
     * @param increment 增量值
     * @return 返回键 key 在执行加一操作之后的值。
     */
    Double incrByFloat(@NonNull CacheKey key, double increment);

    /**
     * 为键 key 储存的数字值减去一。
     *
     * @param key 一定不能为 {@literal null}.
     * @return 在减去增量 1 之后， 键 key 的值。
     */
    Long decr(@NonNull CacheKey key);

    /**
     * 将 key 所储存的值减去减量 decrement 。
     *
     * @param key       一定不能为 {@literal null}.
     * @param decrement 增量值
     * @return 在减去增量 decrement 之后， 键 key 的值。
     */
    Long decrBy(@NonNull CacheKey key, long decrement);
}
