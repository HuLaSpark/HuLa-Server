package com.luohuo.basic.cache.redis2;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import com.luohuo.basic.cache.redis.BaseRedis;
import com.luohuo.basic.model.cache.CacheHashKey;
import com.luohuo.basic.model.cache.CacheKey;
import com.luohuo.basic.utils.ArgumentAssert;
import com.luohuo.basic.utils.CollHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author tangyh
 * @version v1.0
 * @date 2022/10/24 11:21 AM
 * @create [2022/10/24 11:21 AM ] [tangyh] [初始创建]
 */
public class RedisOps extends BaseRedis {

	private static final String LUA_INCR_EXPIRE = "local key,ttl=KEYS[1],ARGV[1] \n" +
			"if redis.call('EXISTS',key)==0 then   \n" +
			"  redis.call('SETEX',key,ttl,1) \n" +
			"  return 1 \n" +
			"else \n" +
			"  return tonumber(redis.call('INCR',key)) \n" +
			"end ";

    public RedisOps(RedisTemplate<String, Object> redisTemplate, StringRedisTemplate stringRedisTemplate, boolean defaultCacheNullVal) {
        super(redisTemplate, stringRedisTemplate, defaultCacheNullVal);
    }


    // ---------------------------- string start ----------------------------

    /**
     * 返回与键 key 相关联的 value 值
     * <p>
     * 如果键 key 不存在， 那么返回特殊值 null ； 否则， 返回键 key 的值。
     *
     * @param key             一定不能为 {@literal null}.
     * @param cacheNullValues 是否缓存空值
     * @return 如果键 key 不存在， 那么返回特殊值 null ； 否则， 返回键 key 的值。
     * @see <a href="https://redis.io/commands/get">Redis Documentation: GET</a>
     */
    public <T> CacheResult<T> get(@NonNull String key, boolean... cacheNullValues) {
        boolean cacheNullVal = getCacheNullVal(cacheNullValues);
        T value = (T) valueOps.get(key);
        if (value == null && cacheNullVal) {
            set(key, newNullVal(), true);
        }

        return new CacheResult<>(key, value);
    }

    /**
     * 返回与键 key 相关联的 value 值
     * <p>
     * 如果值不存在， 那么调用 loader 方法获取数据后，set 到缓存
     *
     * @param key             一定不能为 {@literal null}.
     * @param loader          缓存加载器
     * @param cacheNullValues 是否缓存空值
     * @return 如果redis中没值，先加载loader 的数据，若加载loader 的值为null，直接返回， 否则 设置后loader值后返回。
     * @see <a href="https://redis.io/commands/get">Redis Documentation: GET</a>
     */
    public <T> CacheResult<T> get(@NonNull String key, Function<String, T> loader, boolean... cacheNullValues) {
        boolean cacheNullVal = getCacheNullVal(cacheNullValues);
        T value = (T) valueOps.get(key);
        CacheResult<T> cacheResult = new CacheResult<>(key);
        if (value != null) {
            cacheResult.setRawValue(value);
            return cacheResult;
        } else {
            if (value instanceof Collection && CollUtil.isNotEmpty((Collection<?>) value)) {
                cacheResult.setRawValue(value);
                return cacheResult;
            }
        }
        // 加锁解决缓存击穿
        synchronized (KEY_LOCKS.computeIfAbsent(key, v -> new Object())) {
            value = (T) valueOps.get(key);
            if (value != null) {
                cacheResult.setRawValue(value);
                return cacheResult;
            } else {
                if (value instanceof Collection && CollUtil.isNotEmpty((Collection<?>) value)) {
                    cacheResult.setRawValue(value);
                    return cacheResult;
                }
            }

            try {
                value = loader.apply(key);
                this.set(key, value, cacheNullVal);
            } finally {
                KEY_LOCKS.remove(key);
            }
        }

        cacheResult.setRawValue(value);
        return cacheResult;
    }

    /**
     * 将键 key 的值设为 value ， 并返回键 key 在被设置之前的旧值。
     * <p>
     * 返回给定键 key 的旧值。
     * 如果键 key 没有旧值， 也即是说， 键 key 在被设置之前并不存在， 那么命令返回 nil 。
     * 当键 key 存在但不是字符串类型时， 命令返回一个错误。
     *
     * @param key   一定不能为 {@literal null}.
     * @param value 值
     * @return 如果键 key 不存在， 那么返回特殊值 null ； 否则， 返回给定键 key 的旧值
     * @see <a href="https://redis.io/commands/getset">Redis Documentation: GETSET</a>
     */
    public <T> CacheResult<T> getSet(@NonNull String key, Object value) {
        ArgumentAssert.notNull(key, CACHE_KEY_NOT_NULL);
        T val = (T) valueOps.getAndSet(key, value == null ? newNullVal() : value);

        return new CacheResult<>(key, val);
    }


    /**
     * 返回与键 key 相关联的 value 值
     * <p>
     * 如果键 key 不存在， 那么返回特殊值 null ； 否则， 返回键 key 的值。
     *
     * @param key             一定不能为 {@literal null}.
     * @param cacheNullValues 是否缓存空值
     * @return 如果键 key 不存在， 那么返回特殊值 null ； 否则， 返回键 key 的值。
     * @see <a href="https://redis.io/commands/get">Redis Documentation: GET</a>
     */
    @Nullable
    public <T> CacheResult<T> get(@NonNull CacheKey key, boolean... cacheNullValues) {
        boolean cacheNullVal = getCacheNullVal(cacheNullValues);
        ArgumentAssert.notNull(key, CACHE_KEY_NOT_NULL);
        ArgumentAssert.notNull(key.getKey(), KEY_NOT_NULL);
        T value = (T) valueOps.get(key.getKey());
        if (value == null && cacheNullVal) {
            set(key, newNullVal(), true);
        }
        return new CacheResult<>(key, value);
    }

    /**
     * 返回与键 key 相关联的 value 值
     * <p>
     * 如果键 key 不存在， 那么返回特殊值 null ； 否则， 返回键 key 的值。
     *
     * @param key             一定不能为 {@literal null}.
     * @param loader          加载器
     * @param cacheNullValues 是否缓存空值
     * @return 如果键 key 不存在， 那么返回特殊值 null ； 否则， 返回键 key 的值。
     * @see <a href="https://redis.io/commands/get">Redis Documentation: GET</a>
     */
    @Nullable
    public <T> CacheResult<T> get(@NonNull CacheKey key, Function<CacheKey, ? extends T> loader, boolean... cacheNullValues) {
        ArgumentAssert.notNull(key, CACHE_KEY_NOT_NULL);
        ArgumentAssert.notNull(key.getKey(), KEY_NOT_NULL);
        boolean cacheNullVal = getCacheNullVal(cacheNullValues);
        T value = (T) valueOps.get(key.getKey());

        CacheResult<T> cacheResult = new CacheResult<>(key);
        if (value != null) {
            cacheResult.setRawValue(value);
            return cacheResult;
        } else {
            if (value instanceof Collection && CollUtil.isNotEmpty((Collection<?>) value)) {
                cacheResult.setRawValue(value);
                return cacheResult;
            }
        }
        synchronized (KEY_LOCKS.computeIfAbsent(key.getKey(), v -> new Object())) {
            value = (T) valueOps.get(key.getKey());
            if (value != null) {
                cacheResult.setRawValue(value);
                return cacheResult;
            } else {
                if (value instanceof Collection && CollUtil.isNotEmpty((Collection<?>) value)) {
                    cacheResult.setRawValue(value);
                    return cacheResult;
                }
            }

            try {
                value = loader.apply(key);
                this.set(key, value, cacheNullVal);
            } finally {
                KEY_LOCKS.remove(key.getKey());
            }
        }
        cacheResult.setRawValue(value);
        return cacheResult;
    }


    /**
     * 返回所有(一个或多个)给定 key 的值, 值按请求的键的顺序返回。
     * 如果给定的 key 里面，有某个 key 不存在，那么这个 key 返回特殊值 nil
     *
     * @param keys 一定不能为 {@literal null}.
     * @return 返回一个列表， 列表中包含了所有给定键的值,并按给定key的顺序排列
     * @see <a href="https://redis.io/commands/mget">Redis Documentation: MGET</a>
     */
    public <T> List<CacheResult<T>> mGet(@NonNull String... keys) {
        return mGet(Arrays.asList(keys));
    }

    /**
     * 返回所有(一个或多个)给定 key 的值, 值按请求的键的顺序返回。
     * 如果给定的 key 里面，有某个 key 不存在，那么这个 key 返回特殊值 nil
     *
     * @param keys 一定不能为 {@literal null}.
     * @return 返回一个列表， 列表中包含了所有给定键的值,并按给定key的顺序排列
     * @see <a href="https://redis.io/commands/mget">Redis Documentation: MGET</a>
     */
    public <T> List<CacheResult<T>> mGet(@NonNull CacheKey... keys) {
        return mGetByCacheKey(Arrays.asList(keys));
    }

    /**
     * 返回所有(一个或多个)给定 key 的值, 值按请求的键的顺序返回。
     * 如果给定的 key 里面，有某个 key 不存在，那么这个 key 返回特殊值 nil
     *
     * @param keys 一定不能为 {@literal null}.
     * @return 返回一个列表， 列表中包含了所有给定键的值,并按给定key的顺序排列
     * @see <a href="https://redis.io/commands/mget">Redis Documentation: MGET</a>
     */
    public <T> List<CacheResult<T>> mGet(@NonNull List<String> keys) {
        List<T> list = (List<T>) valueOps.multiGet(keys);
        if (CollUtil.isEmpty(list)) {
            return Collections.emptyList();
        }

        List<CacheResult<T>> resultList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            resultList.add(new CacheResult<>(keys.get(i), list.get(i)));
        }
        return resultList;
    }

    /**
     * 返回所有(一个或多个)给定 key 的值, 值按请求的键的顺序返回。
     * 如果给定的 key 里面，有某个 key 不存在，那么这个 key 返回特殊值 nil
     *
     * @param cacheKeys 一定不能为 {@literal null}.
     * @return 返回一个列表， 列表中包含了所有给定键的值,并按给定key的顺序排列
     * @see <a href="https://redis.io/commands/mget">Redis Documentation: MGET</a>
     */
    public <T> List<CacheResult<T>> mGetByCacheKey(@NonNull Collection<CacheKey> cacheKeys) {
        List<String> keys = cacheKeys.stream().map(CacheKey::getKey).toList();
        return mGet(keys);
    }
    // ---------------------------- string end ----------------------------

    // ---------------------------- hash start ----------------------------

    /**
     * 返回哈希表 key 中给定域 field 的值。
     *
     * @param key             一定不能为 {@literal null}.
     * @param field           一定不能为 {@literal null}.
     * @param cacheNullValues 是否缓存空值
     * @return 默认情况下返回给定域的值, 如果给定域不存在于哈希表中， 又或者给定的哈希表并不存在， 那么命令返回 nil
     * @see <a href="https://redis.io/commands/hget">Redis Documentation: HGET</a>
     */
    @Nullable
    public <T> CacheResult<T> hGet(@NonNull String key, @NonNull Object field, boolean... cacheNullValues) {
        boolean cacheNullVal = getCacheNullVal(cacheNullValues);
        T value = (T) hashOps.get(key, field);
        if (value == null && cacheNullVal) {
            hSet(key, field, newNullVal(), true);
        }
        CacheResult<T> cacheResult = new CacheResult<>(key, value);
        cacheResult.setField(field);
        return cacheResult;
    }

    /**
     * 返回哈希表 key 中给定域 field 的值。
     *
     * @param key             一定不能为 {@literal null}.
     * @param field           一定不能为 {@literal null}.
     * @param loader          加载器
     * @param cacheNullValues 是否缓存空值
     * @return 默认情况下返回给定域的值, 如果给定域不存在于哈希表中， 又或者给定的哈希表并不存在， 那么命令返回 nil
     * @see <a href="https://redis.io/commands/hget">Redis Documentation: HGET</a>
     */
    @Nullable
    public <T> CacheResult<T> hGet(@NonNull String key, @NonNull Object field, BiFunction<String, Object, T> loader, boolean... cacheNullValues) {
        boolean cacheNullVal = getCacheNullVal(cacheNullValues);
        T value = (T) hashOps.get(key, field);

        CacheResult<T> cacheResult = new CacheResult<>(key, value);
        cacheResult.setField(field);
        if (value != null) {
            cacheResult.setRawValue(value);
            return cacheResult;
        } else {
            if (value instanceof Collection && CollUtil.isNotEmpty((Collection<?>) value)) {
                cacheResult.setRawValue(value);
                return cacheResult;
            }
        }

        String lockKey = key + "@" + field;
        synchronized (KEY_LOCKS.computeIfAbsent(lockKey, v -> new Object())) {
            value = (T) hashOps.get(key, field);
            if (value != null) {
                cacheResult.setRawValue(value);
                return cacheResult;
            } else {
                if (value instanceof Collection && CollUtil.isNotEmpty((Collection<?>) value)) {
                    cacheResult.setRawValue(value);
                    return cacheResult;
                }
            }

            try {
                value = loader.apply(key, field);
                this.hSet(key, field, value, cacheNullVal);
            } finally {
                KEY_LOCKS.remove(lockKey);
            }
        }
        cacheResult.setRawValue(value);
        return cacheResult;
    }

    /**
     * 返回哈希表 key 中给定域 field 的值。
     *
     * @param key             一定不能为 {@literal null}.
     * @param cacheNullValues 是否缓存空值
     * @return 默认情况下返回给定域的值, 如果给定域不存在于哈希表中， 又或者给定的哈希表并不存在， 那么命令返回 nil
     * @see <a href="https://redis.io/commands/hget">Redis Documentation: HGET</a>
     */
    @Nullable
    public <T> CacheResult<T> hGet(@NonNull CacheHashKey key, boolean... cacheNullValues) {
        boolean cacheNullVal = getCacheNullVal(cacheNullValues);
        ArgumentAssert.notNull(key, "CacheHashKey不能为空");
        ArgumentAssert.notEmpty(key.getKey(), KEY_NOT_NULL);
        ArgumentAssert.notNull(key.getField(), "field不能为空");

        T value = (T) hashOps.get(key.getKey(), key.getField());
        if (value == null && cacheNullVal) {
            hSet(key, newNullVal(), true);
        }
        // NullVal 值
        return new CacheResult<>(key, value);
    }

    /**
     * 返回哈希表 key 中给定域 field 的值。
     *
     * @param key             一定不能为 {@literal null}.
     * @param cacheNullValues 是否缓存空值
     * @param loader          加载器
     * @return 默认情况下返回给定域的值, 如果给定域不存在于哈希表中， 又或者给定的哈希表并不存在， 那么命令返回 nil
     * @see <a href="https://redis.io/commands/hget">Redis Documentation: HGET</a>
     */
    @Nullable
    public <T> CacheResult<T> hGet(@NonNull CacheHashKey key, Function<CacheHashKey, T> loader, boolean... cacheNullValues) {
        boolean cacheNullVal = getCacheNullVal(cacheNullValues);
        T value = (T) hashOps.get(key.getKey(), key.getField());

		CacheResult<T> cacheResult = new CacheResult<>(key, value);
		if (ObjectUtil.isNotNull(value) && (value instanceof Collection && !((Collection<?>) value).isEmpty())) {
			cacheResult.setRawValue(value);
			return cacheResult;
		}
        String lockKey = key.getKey() + "@" + key.getField();
        synchronized (KEY_LOCKS.computeIfAbsent(lockKey, v -> new Object())) {
            value = (T) hashOps.get(key.getKey(), key.getField());
            if (value != null) {
                cacheResult.setRawValue(value);
                return cacheResult;
            }
            try {
                value = loader.apply(key);
                this.hSet(key, value, cacheNullVal);
            } finally {
                KEY_LOCKS.remove(key.getKey());
            }
        }
        cacheResult.setRawValue(value);
        return cacheResult;
    }


    /**
     * 返回哈希表 key 中，一个或多个给定域的值。
     * 如果给定的域不存在于哈希表，那么返回一个 nil 值。
     * 因为不存在的 key 被当作一个空哈希表来处理，所以对一个不存在的 key 进行 HMGET 操作将返回一个只带有 nil 值的表。
     *
     * @param key    一定不能为 {@literal null}.
     * @param fields 一定不能为 {@literal null}.
     * @see <a href="https://redis.io/commands/hmget">Redis Documentation: hmget</a>
     */
    public List<CacheResult<Object>> hmGet(@NonNull String key, @NonNull Object... fields) {
        return hmGet(key, Arrays.asList(fields));
    }

    /**
     * 返回哈希表 key 中，一个或多个给定域的值。
     * 如果给定的域不存在于哈希表，那么返回一个 nil 值。
     * 因为不存在的 key 被当作一个空哈希表来处理，所以对一个不存在的 key 进行 HMGET 操作将返回一个只带有 nil 值的表。
     *
     * @param key    一定不能为 {@literal null}.
     * @param fields 一定不能为 {@literal null}.
     * @see <a href="https://redis.io/commands/hmget">Redis Documentation: hmget</a>
     */
    public List<CacheResult<Object>> hmGet(@NonNull String key, @NonNull List<Object> fields) {
        List<Object> list = hashOps.multiGet(key, fields);
        List<CacheResult<Object>> result = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            CacheResult<Object> cacheResult = new CacheResult<>(key, list.get(i));
            cacheResult.setField(fields.get(i));
            result.add(cacheResult);
        }
        return result;
    }


    /**
     * 返回哈希表 key 中所有域的值。
     *
     * @param key 一定不能为 {@literal null}.
     * @return 一个包含哈希表中所有值的表。
     * @see <a href="https://redis.io/commands/hvals">Redis Documentation: hvals</a>
     */
    public <HV> List<CacheResult<HV>> hVals(@NonNull String key) {
        List<HV> list = (List<HV>) hashOps.values(key);
        return list.stream().map(o -> new CacheResult<>(key, o)).toList();

    }


    /**
     * 返回哈希表 key 中，所有的域和值。
     * 在返回值里，紧跟每个域名(field name)之后是域的值(value)，所以返回值的长度是哈希表大小的两倍。
     *
     * @param key 一定不能为 {@literal null}.
     * @return 以列表形式返回哈希表的域和域的值
     * @see <a href="https://redis.io/commands/hgetall">Redis Documentation: hgetall</a>
     */
    public <K, V> Map<K, CacheResult<V>> hGetAll(@NonNull String key) {
        Map<K, V> map = (Map<K, V>) hashOps.entries(key);
        return returnMapVal(key, map);
    }

    public <K, V> Map<K, CacheResult<V>> hGetAll(@NonNull CacheHashKey key) {
        return hGetAll(key.getKey());
    }


    /**
     * 返回哈希表 key 中给定域 field 的值。
     *
     * @param key             一定不能为 {@literal null}.
     * @param cacheNullValues 是否缓存空值
     * @param loader          加载器
     * @return 默认情况下返回给定域的值, 如果给定域不存在于哈希表中， 又或者给定的哈希表并不存在， 那么命令返回 nil
     * @see <a href="https://redis.io/commands/hget">Redis Documentation: HGET</a>
     */
    @Nullable
    public <K, V> Map<K, CacheResult<V>> hGetAll(@NonNull CacheHashKey key, Function<CacheHashKey, Map<K, V>> loader, boolean... cacheNullValues) {
        boolean cacheNullVal = getCacheNullVal(cacheNullValues);
        Map<K, V> map = (Map<K, V>) hashOps.entries(key.getKey());
        if (MapUtil.isNotEmpty(map)) {
            return returnMapVal(key.getKey(), map);
        }
        String lockKey = key.getKey();
        synchronized (KEY_LOCKS.computeIfAbsent(lockKey, v -> new Object())) {
            map = (Map<K, V>) hashOps.entries(key.getKey());
            if (MapUtil.isNotEmpty(map)) {
                return returnMapVal(key.getKey(), map);
            }
            try {
                map = loader.apply(key);
                this.hmSet(key.getKey(), map, cacheNullVal);
            } finally {
                KEY_LOCKS.remove(key.getKey());
            }
        }
        return returnMapVal(key.getKey(), map);
    }

    // ---------------------------- hash end ----------------------------

    private <K, V> Map<K, CacheResult<V>> returnMapVal(String key, Map<K, V> map) {
        Map<K, CacheResult<V>> newMap = new HashMap<>(CollHelper.initialCapacity(map.size()));
        if (MapUtil.isNotEmpty(map)) {
            map.forEach((field, v) -> {
                CacheResult<V> cacheResult = new CacheResult<>(key, v);
                newMap.put(field, cacheResult);

            });
        }

        return newMap;
    }

    private boolean getCacheNullVal(boolean... cacheNullValues) {
        return cacheNullValues.length > 0 ? cacheNullValues[0] : defaultCacheNullVal;
    }

	public List<Object> multiGet(List<String> list) {
		return valueOps.multiGet(list);
	}

	public int integerInc(String key, int time, TimeUnit unit) {
		RedisScript<Long> redisScript = new DefaultRedisScript<>(LUA_INCR_EXPIRE, Long.class);
		try {
			Long result = stringRedisTemplate.execute(redisScript, Collections.singletonList(key), String.valueOf(unit.toSeconds(time)));
			return Integer.parseInt(result.toString());
		} catch (Exception e) {
			del(key);
			throw e;
		}
	}
}
