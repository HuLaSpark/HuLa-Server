package com.luohuo.basic.cache.repository.impl;

import cn.hutool.core.util.StrUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.lang.NonNull;
import com.luohuo.basic.cache.redis2.CacheResult;
import com.luohuo.basic.cache.repository.CacheOps;
import com.luohuo.basic.cache.repository.CachePlusOps;
import com.luohuo.basic.model.cache.CacheHashKey;
import com.luohuo.basic.model.cache.CacheKey;
import com.luohuo.basic.utils.StrPool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;


/**
 * 基于 Caffeine 实现的内存缓存， 主要用于开发、测试、演示环境
 * 生产环境慎用！
 *
 * @author 乾乾
 * @date 2019/08/07
 */
public class CaffeineOpsImpl implements CacheOps, CachePlusOps {
    /**
     * 最大数量
     */
    private static final long DEF_MAX_SIZE = 1_000;

    /**
     * 为什么不直接用 Cache<String, Object> ？
     * 因为想针对每一个key单独设置过期时间
     */
    private final Cache<String, Cache<String, Object>> cacheMap = Caffeine.newBuilder()
            .maximumSize(DEF_MAX_SIZE)
            .build();

    @Override
    public Long del(@NonNull CacheKey... keys) {
        for (CacheKey key : keys) {
            cacheMap.invalidate(key.getKey());
        }
        return (long) keys.length;
    }

    @Override
    public Long del(@NonNull Collection<CacheKey> keys) {
        for (CacheKey key : keys) {
            cacheMap.invalidate(key.getKey());
        }
        return (long) keys.size();
    }

    @Override
    public Long del(String... keys) {
        for (String key : keys) {
            cacheMap.invalidate(key);
        }
        return (long) keys.length;
    }

    @Override
    public void set(@NonNull CacheKey key, Object value, boolean... cacheNullValues) {
        if (value == null) {
            return;
        }
        Caffeine<Object, Object> builder = Caffeine.newBuilder()
                .maximumSize(DEF_MAX_SIZE);
        if (key.getExpire() != null) {
            builder.expireAfterWrite(key.getExpire());
        }
        Cache<String, Object> cache = builder.build();
        cache.put(key.getKey(), value);
        cacheMap.put(key.getKey(), cache);
    }

    @Override
    public <T> CacheResult<T> get(@NonNull CacheKey key, boolean... cacheNullValues) {
        return get(key.getKey(), cacheNullValues);
    }

    @Override
    public <T> CacheResult<T> get(String key, boolean... cacheNullValues) {
        Cache<String, Object> ifPresent = cacheMap.getIfPresent(key);
        if (ifPresent == null) {
            return null;
        }
        CacheResult<T> result = new CacheResult<>(key);
        result.setRawValue((T) ifPresent.getIfPresent(key));
        return result;
    }

    @Override
    public <T> List<CacheResult<T>> find(@NonNull Collection<CacheKey> keys) {
        return keys.stream().map(k -> (CacheResult<T>) get(k, false)).filter(Objects::nonNull).toList();
    }

    @Override
    public <T> CacheResult<T> get(@NonNull CacheKey key, Function<CacheKey, ? extends T> loader, boolean... cacheNullValues) {
        Cache<String, Object> cache = cacheMap.get(key.getKey(), (k) -> {
            Caffeine<Object, Object> builder = Caffeine.newBuilder()
                    .maximumSize(DEF_MAX_SIZE);
            if (key.getExpire() != null) {
                builder.expireAfterWrite(key.getExpire());
            }
            Cache<String, Object> newCache = builder.build();
            newCache.get(k, (tk) -> loader.apply(new CacheKey(tk)));
            return newCache;
        });

        CacheResult<T> result = new CacheResult<>(key.getKey());
        result.setRawValue((T) cache.getIfPresent(key.getKey()));
        return result;
    }

    @Override
    public void flushDb() {
        cacheMap.invalidateAll();
    }

    @Override
    public Boolean exists(@NonNull final CacheKey key) {
        Cache<String, Object> cache = cacheMap.getIfPresent(key.getKey());
        if (cache == null) {
            return false;
        }
        cache.cleanUp();
        return cache.estimatedSize() > 0;
    }

    @Override
    public Long incr(@NonNull CacheKey key) {
        CacheResult<Long> old = get(key, k -> 0L);
        Long newVal = old.getValue() + 1;
        set(key, newVal);
        return newVal;
    }

    @Override
    public Long getCounter(CacheKey key, Function<CacheKey, Long> loader) {
        return (Long) get(key).getValue();
    }

    @Override
    public Long incrBy(@NonNull CacheKey key, long increment) {
        CacheResult<Long> old = get(key, k -> 0L);
        Long newVal = old.getValue() + increment;
        set(key, newVal);
        return newVal;
    }

    @Override
    public Double incrByFloat(@NonNull CacheKey key, double increment) {
        CacheResult<Double> old = get(key, k -> 0D);
        Double newVal = old.getValue() + increment;
        set(key, newVal);
        return newVal;
    }

    @Override
    public Long decr(@NonNull CacheKey key) {
        CacheResult<Long> old = get(key, k -> 0L);
        Long newVal = old.getValue() - 1;
        set(key, newVal);
        return newVal;
    }

    @Override
    public Long decrBy(@NonNull CacheKey key, long decrement) {
        CacheResult<Long> old = get(key, k -> 0L);
        Long newVal = old.getValue() - decrement;
        set(key, newVal);
        return newVal;
    }
    // ---- 以下接口可能有问题，仅支持在开发环境使用

    /**
     * KEYS * 匹配数据库中所有 key 。
     * KEYS h?llo 匹配 hello ， hallo 和 hxllo 等。
     * KEYS h*llo 匹配 hllo 和 heeeeello 等。
     * KEYS h[ae]llo 匹配 hello 和 hallo ，但不匹配 hillo
     *
     * @param pattern 表达式
     * @return 集合
     */
    @Override
    public Set<String> keys(@NonNull String pattern) {
        if (StrUtil.isEmpty(pattern)) {
            return Collections.emptySet();
        }
        ConcurrentMap<String, Cache<String, Object>> map = cacheMap.asMap();
        Set<String> list = new HashSet<>();
        map.forEach((k, val) -> {
            // *
            if (StrPool.ASTERISK.equals(pattern)) {
                list.add(k);
                return;
            }
            // h?llo
            if (pattern.contains(StrPool.QUESTION_MARK)) {
                //待实现
                return;
            }
            // h*llo
            if (pattern.contains(StrPool.ASTERISK)) {
                //待实现
                return;
            }
            // h[ae]llo
            if (pattern.contains(StrPool.LEFT_SQ_BRACKET) && pattern.contains(StrPool.RIGHT_SQ_BRACKET)) {
                //待实现
                return;
            }
        });
        return list;
    }

    @Override
    public List<String> scan(String pattern) {
        return new ArrayList<>(keys(pattern));
    }

    @Override
    public void scanUnlink(String pattern) {
        Set<String> keys = keys(pattern);
        del(keys.stream().toArray(String[]::new));
    }

    @Override
    public Boolean expire(@NonNull CacheKey key) {
        return true;
    }

    @Override
    public Boolean persist(@NonNull CacheKey key) {
        return true;
    }

    @Override
    public String type(@NonNull CacheKey key) {
        return "caffeine";
    }

    @Override
    public Long ttl(@NonNull CacheKey key) {
        return -1L;
    }

    @Override
    public Long pTtl(@NonNull CacheKey key) {
        return -1L;
    }

    @Override
    public void hSet(@NonNull CacheHashKey key, Object value, boolean... cacheNullValues) {
        this.set(key.tran(), value, cacheNullValues);
    }

    @Override
    public <T> CacheResult<T> hGet(@NonNull CacheHashKey key, boolean... cacheNullValues) {
        return get(key.tran(), cacheNullValues);
    }

    @Override
    public <T> CacheResult<T> hGet(@NonNull CacheHashKey key, Function<CacheHashKey, T> loader, boolean... cacheNullValues) {
        Function<CacheKey, T> ckLoader = k -> loader.apply(key);
        return get(key.tran(), ckLoader, cacheNullValues);
    }

    @Override
    public Boolean hExists(@NonNull CacheHashKey cacheHashKey) {
        return exists(cacheHashKey.tran());
    }

    @Override
    public Long hDel(@NonNull String key, Object... fields) {
        for (Object field : fields) {
            cacheMap.invalidate(StrUtil.join(StrUtil.COLON, key, field));
        }
        return (long) fields.length;
    }

    @Override
    public Long hDel(@NonNull CacheHashKey cacheHashKey) {
        cacheMap.invalidate(cacheHashKey.tran().getKey());
        return 1L;
    }

    @Override
    public Long hLen(@NonNull CacheHashKey key) {
        return 0L;
    }

    @Override
    public Long hIncrBy(@NonNull CacheHashKey key, long increment) {
        return incrBy(key.tran(), increment);
    }

    @Override
    public Double hIncrBy(@NonNull CacheHashKey key, double increment) {
        return incrByFloat(key.tran(), increment);
    }

    @Override
    public <HK> Set<HK> hKeys(@NonNull CacheHashKey key) {
        return Collections.emptySet();
    }

    @Override
    public <HV> List<CacheResult<HV>> hVals(@NonNull CacheHashKey key) {
        return Collections.emptyList();
    }

    @Override
    public <K, V> Map<K, CacheResult<V>> hGetAll(CacheHashKey key) {
        return Collections.emptyMap();
    }

    @Override
    public <K, V> Map<K, CacheResult<V>> hGetAll(CacheHashKey key, Function<CacheHashKey, Map<K, V>> loader, boolean... cacheNullValues) {
        return Collections.emptyMap();
    }

    @Override
    public Long sAdd(@NonNull CacheKey key, Object value) {
        return 0L;
    }

    @Override
    public Long sRem(@NonNull CacheKey key, Object... members) {
        return 0L;
    }

    @Override
    public Set<Object> sMembers(@NonNull CacheKey key) {
        return Collections.emptySet();
    }

	@Override
	public Boolean sIsMember(CacheKey key, Object value) {
		return null;
	}

	@Override
    public <T> T sPop(@NonNull CacheKey key) {
        return null;
    }

    @Override
    public Long sCard(@NonNull CacheKey key) {
        return 0L;
    }

	@Override
	public List<Long> sMultiCard(List<String> keys) {
		return List.of();
	}

	@Override
	public List<Object> getZSetScores(String key, List<Long> fields) {
		return List.of();
	}

	@Override
	public <T> List<CacheResult<T>> mGet(List<String> frequencyKeys) {
		return List.of();
	}

	@Override
	public Long inc(String k, Integer time, TimeUnit unit) {
		return 0L;
	}

	@Override
	public <T> List<T> mGet(Collection<String> keys, Class<T> tClass) {
		return List.of();
	}

	@Override
	public <T> void mSet(Map<String, T> map, long time) {

	}

	@Override
	public Long zCard(String key) {
		return 0L;
	}

	@Override
	public Long zRemove(String key, Object value) {
		return 0L;
	}

	@Override
	public Boolean zAdd(String key, Object value, double score) {
		return null;
	}

	@Override
	public Boolean zAdd(String key, String value, double score) {
		return null;
	}

	@Override
	public Set<Object> zAll(String key) {
		return Set.of();
	}

	@Override
	public Boolean zIsMember(String key, Object value) {
		return null;
	}

	@Override
	public Long sSize(String key) {
		return 0L;
	}

	@Override
	public Set<ZSetOperations.TypedTuple<Object>> zReverseRangeWithScores(String key, long pageSize) {
		return Set.of();
	}

	@Override
	public Set<ZSetOperations.TypedTuple<Object>> zRangeByScoreWithScores(String key, Double hotStart, Double hotEnd) {
		return Set.of();
	}

	@Override
	public Set<ZSetOperations.TypedTuple<Object>> zRangeByScoreWithScores(String key, Double hotStart, Double hotEnd, long offset, long count) {
		return Set.of();
	}

	@Override
	public Set<ZSetOperations.TypedTuple<Object>> zReverseRangeByScoreWithScores(String redisKey, double v, Integer pageSize) {
		return Set.of();
	}

	@Override
	public Long lexCount(String key, Range<String> range) {
		return 0L;
	}

	@Override
	public int integerInc(String loginCode, int minutes, TimeUnit timeUnit) {
		return 0;
	}

	@Override
	public void ZSetAddAndExpire(String key, long start, long length, long current) {

	}
}
