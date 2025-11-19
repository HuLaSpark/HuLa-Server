package com.luohuo.basic.cache.repository.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.luohuo.basic.jackson.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.lang.NonNull;
import com.luohuo.basic.cache.redis2.CacheResult;
import com.luohuo.basic.cache.redis2.RedisOps;
import com.luohuo.basic.cache.repository.CacheOps;
import com.luohuo.basic.cache.repository.CachePlusOps;
import com.luohuo.basic.model.cache.CacheHashKey;
import com.luohuo.basic.model.cache.CacheKey;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Redis Repository
 * redis 基本操作 可扩展,基本够用了
 *
 * @author 乾乾
 * @date 2019-08-06 10:42
 */
@Slf4j
public class RedisOpsImpl implements CacheOps, CachePlusOps {

	private static final ObjectMapper mapper = JsonUtil.newInstance();

    /**
     * Spring Redis Template
     */
    private final RedisOps redisOps;

    public RedisOpsImpl(RedisOps redisOps) {
        this.redisOps = redisOps;
    }

    /**
     * 获取 RedisTemplate对象
     */
    public RedisTemplate<String, Object> getRedisTemplate() {
        return redisOps.getRedisTemplate();
    }

    @Override
    public Long del(@NonNull CacheKey... keys) {
        return redisOps.del(keys);
    }

    @Override
    public Long del(@NonNull Collection<CacheKey> keys) {
        return redisOps.del(keys);
    }

    @Override
    public Long del(String... keys) {
        return redisOps.del(keys);
    }

    @Override
    public Boolean exists(@NonNull CacheKey key) {
        return redisOps.exists(key.getKey());
    }

    @Override
    public void set(@NonNull CacheKey key, Object value, boolean... cacheNullValues) {
        redisOps.set(key, value, cacheNullValues);
    }

    @Override
    public <T> CacheResult<T> get(@NonNull CacheKey key, boolean... cacheNullValues) {
        return redisOps.get(key, cacheNullValues);
    }

    @Override
    public <T> CacheResult<T> get(@NonNull String key, boolean... cacheNullValues) {
        return redisOps.get(key, cacheNullValues);
    }

    @Override
    public <T> List<CacheResult<T>> find(@NonNull Collection<CacheKey> keys) {
        return redisOps.mGetByCacheKey(keys);
    }

    @Override
    public <T> CacheResult<T> get(@NonNull CacheKey key, Function<CacheKey, ? extends T> loader, boolean... cacheNullValues) {
        return redisOps.get(key, loader, cacheNullValues);
    }

    /**
     * 清空redis存储的数据
     */
    @Override
    public void flushDb() {
        redisOps.getRedisTemplate().execute((RedisCallback<String>) connection -> {
            connection.flushDb();
            return "ok";
        });
    }

    @Override
    public Long incr(@NonNull CacheKey key) {
        return redisOps.incr(key);
    }

    @Override
    public Long getCounter(CacheKey key, Function<CacheKey, Long> loader) {
        return redisOps.getCounter(key, loader);
    }

    @Override
    public Long incrBy(@NonNull CacheKey key, long increment) {
        return redisOps.incrBy(key, increment);
    }

    @Override
    public Double incrByFloat(@NonNull CacheKey key, double increment) {
        return redisOps.incrByFloat(key, increment);
    }

    @Override
    public Long decr(@NonNull CacheKey key) {
        return redisOps.decr(key);
    }

    @Override
    public Long decrBy(@NonNull CacheKey key, long decrement) {
        return redisOps.decrBy(key, decrement);
    }

    @Override
    public Set<String> keys(@NonNull String pattern) {
        return redisOps.keys(pattern);
    }

    @Override
    public List<String> scan(@NonNull String pattern) {
        return redisOps.scan(pattern);
    }

    @Override
    public void scanUnlink(@NonNull String pattern) {
        redisOps.scanUnlink(pattern);
    }

    @Override
    public Boolean expire(@NonNull CacheKey key) {
        assert key.getExpire() != null;
        return redisOps.expire(key.getKey(), key.getExpire());
    }

    @Override
    public Boolean persist(@NonNull CacheKey key) {
        return redisOps.persist(key.getKey());
    }

    @Override
    public String type(@NonNull CacheKey key) {
        return redisOps.type(key.getKey());
    }

    @Override
    public Long ttl(@NonNull CacheKey key) {
        return redisOps.ttl(key.getKey());
    }

    @Override
    public Long pTtl(@NonNull CacheKey key) {
        return redisOps.pTtl(key.getKey());
    }

    @Override
    public void hSet(@NonNull CacheHashKey key, Object value, boolean... cacheNullValues) {
        redisOps.hSet(key, value, cacheNullValues);
    }

    @Override
    public <T> CacheResult<T> hGet(@NonNull CacheHashKey key, boolean... cacheNullValues) {
        return redisOps.hGet(key, cacheNullValues);
    }

    @Override
    public <T> CacheResult<T> hGet(@NonNull CacheHashKey key, Function<CacheHashKey, T> loader, boolean... cacheNullValues) {
        return redisOps.hGet(key, loader, cacheNullValues);
    }

    @Override
    public Boolean hExists(@NonNull CacheHashKey cacheHashKey) {
        return redisOps.hExists(cacheHashKey);
    }

    @Override
    public Long hDel(@NonNull String key, Object... fields) {
        return redisOps.hDel(key, fields);
    }

    @Override
    public Long hDel(@NonNull CacheHashKey cacheHashKey) {
        return redisOps.hDel(cacheHashKey.getKey(), cacheHashKey.getField());
    }

    @Override
    public Long hLen(@NonNull CacheHashKey key) {
        return redisOps.hLen(key.getKey());
    }

    @Override
    public Long hIncrBy(@NonNull CacheHashKey key, long increment) {
        return redisOps.hIncrBy(key, increment);
    }

    @Override
    public Double hIncrBy(@NonNull CacheHashKey key, double increment) {
        return redisOps.hIncrByFloat(key, increment);
    }

    @Override
    public <HK> Set<HK> hKeys(@NonNull CacheHashKey key) {
        return redisOps.hKeys(key.getKey());
    }

    @Override
    public <HV> List<CacheResult<HV>> hVals(@NonNull CacheHashKey key) {
        return redisOps.hVals(key.getKey());
    }


    @Override
    public <K, V> Map<K, CacheResult<V>> hGetAll(@NonNull CacheHashKey key) {
        return redisOps.hGetAll(key);
    }

    @Override
    public <K, V> Map<K, CacheResult<V>> hGetAll(@NonNull CacheHashKey key, Function<CacheHashKey, Map<K, V>> loader, boolean... cacheNullValues) {
        return redisOps.hGetAll(key, loader, cacheNullValues);
    }


    @Override
    public Long sAdd(@NonNull CacheKey key, Object value) {
        Long result = redisOps.sAdd(key, value);
        if (key.getExpire() != null && key.getExpire().getSeconds() > 0) {
            redisOps.expire(key.getKey(), key.getExpire());
        }
        return result;
    }

	/**
	 * 获取set缓存的长度
	 *
	 * @param key 键
	 * @return
	 */
	public Long sSize(String key) {
		try {
			return redisOps.sSize(key);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return 0L;
		}
	}

    @Override
    public Long sRem(@NonNull CacheKey key, Object... members) {
        return redisOps.sRem(key, members);
    }

    @Override
    public Set<Object> sMembers(@NonNull CacheKey key) {
        return redisOps.sMembers(key);
    }

	@Override
	public Boolean sIsMember(CacheKey key, Object member) {
		return redisOps.sIsMember(key, member);
	}

	@Override
    public <T> T sPop(@NonNull CacheKey key) {
        return redisOps.sPop(key);
    }

    @Override
    public Long sCard(@NonNull CacheKey key) {
        return redisOps.sCard(key);
    }

	@Override
	public List<Long> sMultiCard(List<String> keys) {
		return redisOps.sMultiCard(keys);
	}

	@Override
	public List<Object> getZSetScores(String key, List<Long> fields) {
		return redisOps.getZSetScores(key, fields);
	}

	@Override
	public <T> List<CacheResult<T>> mGet(List<String> frequencyKeys) {
		return redisOps.mGet(frequencyKeys);
	}

	@Override
	public Long inc(String k, Integer time, TimeUnit unit) {
		return redisOps.inc(k, time, unit);
	}

	public static <T> List<T> toBeanOrNull(List<Object> list, Class<T> tClass) {
		if (list == null || list.isEmpty()) {
			return Collections.emptyList();
		}

		List<T> result = new ArrayList<>(list.size());
		for (Object obj : list) {
			try {
				T convertedObj = mapper.convertValue(obj, tClass);
				result.add(convertedObj);
			} catch (Exception e) {
				log.error("Failed to convert object to type {}: {}", tClass.getSimpleName(), e.getMessage());
				return null;
			}
		}
		return result;
	}

	@Override
	public <T> List<T> mGet(Collection<String> keys, Class<T> tClass) {
		List<Object> list = redisOps.multiGet(keys.stream().collect(Collectors.toList()));
		if (Objects.isNull(list)) {
			return new ArrayList<>();
		}
		return toBeanOrNull(list, tClass);
	}

	@Override
	public <T> void mSet(Map<String, T> map, long time) {
		redisOps.mSet(map, time);
	}

	@Override
	public Long zCard(String key) {
		return redisOps.zCard(key);
	}

	@Override
	public Long zRemove(String key, Object value) {
		return redisOps.zRem(key, value.toString());
	}

	@Override
	public Boolean zAdd(String key, Object value, double score) {
		return zAdd(key, value.toString(), score);
	}

	@Override
	public Boolean zAdd(String key, String value, double score) {
		return redisOps.zAdd(key, value, score);
	}

	@Override
	public Set<Object> zAll(String key) {
		return redisOps.zRange(key, 0, -1);
	}

	@Override
	public Boolean zIsMember(String key, Object value) {
		return Objects.nonNull(redisOps.zScore(key, value.toString()));
	}

	/**
	 * 根据Score值查询集合元素, 从小到大排序
	 *
	 * @param key
	 * @param min 最小值
	 * @param max 最大值
	 * @return
	 */
	public Set<ZSetOperations.TypedTuple<Object>> zRangeByScoreWithScores(String key, Double min, Double max) {
		if (Objects.isNull(min)) {
			min = Double.MIN_VALUE;
		}
		if (Objects.isNull(max)) {
			max = Double.MAX_VALUE;
		}
		return redisOps.zRangeByScoreWithScores(key, min, max);
	}

	/**
	 * 根据Score值查询集合元素, 从小到大排序
	 *
	 * @param key
	 * @param min 最小值
	 * @param max 最大值
	 * @param offset 游标
	 * @param count 数量
	 * @return
	 */
	public Set<ZSetOperations.TypedTuple<Object>> zRangeByScoreWithScores(String key, Double min, Double max, long offset, long count) {
		if (Objects.isNull(min)) {
			min = Double.MIN_VALUE;
		}
		if (Objects.isNull(max)) {
			max = Double.MAX_VALUE;
		}
		return redisOps.zRangeByScoreWithScores(key, min, max, offset, count);
	}

	/**
	 * 获取集合的元素, 从大到小排序, 并返回score值
	 *
	 * @param key
	 * @param pageSize
	 * @return
	 */
	public Set<ZSetOperations.TypedTuple<Object>> zReverseRangeWithScores(String key, long pageSize) {
		return redisOps.zReverseRangeByScoreWithScores(key, Double.MIN_VALUE, Double.MAX_VALUE, 0, pageSize);
	}

	public Set<ZSetOperations.TypedTuple<Object>> zReverseRangeByScoreWithScores(String redisKey, double max, Integer pageSize) {
		return redisOps.zReverseRangeByScoreWithScores(redisKey, Double.MIN_VALUE, max, 1, pageSize);
	}

	public Long lexCount(String key, Range<String> range) {
		return redisOps.lexCount(key, range);
	}

	public int integerInc(String loginCode, int minutes, TimeUnit timeUnit) {
		return redisOps.integerInc(loginCode, minutes, timeUnit);
	}

	@Override
	public void ZSetAddAndExpire(String key, long start, long length, long current) {
		redisOps.ZSetAddAndExpire(key, start, length, current);
	}
}
