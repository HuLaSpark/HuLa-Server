package com.luohuo.basic.cache.redis;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import com.luohuo.basic.utils.JsonUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import com.luohuo.basic.cache.redis2.CacheResult;
import com.luohuo.basic.exception.BizException;
import com.luohuo.basic.model.cache.CacheHashKey;
import com.luohuo.basic.model.cache.CacheKey;
import com.luohuo.basic.utils.ArgumentAssert;
import com.luohuo.basic.utils.CollHelper;
import com.luohuo.basic.utils.StrPool;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 公共的redis方法
 * <p>
 * 只对redis 中 string 和 hash 类型的缓存 做了缓存空值处理，其他类型未缓存空值
 *
 * @author tangyh
 * @version v1.0
 * @date 2022/10/24 11:23 AM
 * @create [2022/10/24 11:23 AM ] [tangyh] [初始创建]
 */
@Slf4j
@Getter
public abstract class BaseRedis {

    protected static final String KEY_NOT_NULL = "key不能为空";
    protected static final String CACHE_KEY_NOT_NULL = "cacheKey不能为空";
    protected static final int BATCH_SIZE = 1000;

    protected static final Map<String, Object> KEY_LOCKS = new ConcurrentHashMap<>();
    protected final ValueOperations<String, Object> valueOps;
    protected final HashOperations<String, Object, Object> hashOps;
    protected final ListOperations<String, Object> listOps;
    protected final SetOperations<String, Object> setOps;
    protected final ZSetOperations<String, Object> zSetOps;
    protected final StringRedisTemplate stringRedisTemplate;
    /**
     * 全局配置是否缓存null值
     */
    protected final boolean defaultCacheNullVal;
    protected RedisTemplate<String, Object> redisTemplate;

    public BaseRedis(RedisTemplate<String, Object> redisTemplate, StringRedisTemplate stringRedisTemplate, boolean defaultCacheNullVal) {
        this.redisTemplate = redisTemplate;
        ArgumentAssert.notNull(redisTemplate, "redisTemplate 为空");
        valueOps = redisTemplate.opsForValue();
        hashOps = redisTemplate.opsForHash();
        listOps = redisTemplate.opsForList();
        setOps = redisTemplate.opsForSet();
        zSetOps = redisTemplate.opsForZSet();
        this.stringRedisTemplate = stringRedisTemplate;
        this.defaultCacheNullVal = defaultCacheNullVal;
    }

    /**
     * 判断缓存值是否为空对象
     *
     * @param value 返回值
     * @return 是否为空
     */
    protected static <T> boolean isNullVal(T value) {
        boolean isNull = value == null || NullVal.class.equals(value.getClass());
        return isNull || value.getClass().equals(Object.class) || (value instanceof Map map && map.isEmpty());
    }

    /**
     * new 一个空值
     *
     * @return 空对象
     */
    protected NullVal newNullVal() {
        return new NullVal();
    }

    protected void setExpire(CacheKey key) {
        if (key != null && key.getExpire() != null && key.getExpire().getSeconds() > 0) {
            redisTemplate.expire(key.getKey(), key.getExpire());
        }
    }

    // ---------------------------- common start ----------------------------

    /**
     * 删除给定的一个 key 或 多个key
     * 不存在的 key 会被忽略。
     *
     * @param keys 一定不能为 {@literal null}.
     * @return key 被删除返回true
     * @see <a href="https://redis.io/commands/del">Redis Documentation: DEL</a>
     */
    public Long del(@NonNull CacheKey... keys) {
        return del(Arrays.stream(keys).map(CacheKey::getKey).toList());
    }

    /**
     * 删除给定的一个 key 或 多个key
     * 不存在的 key 会被忽略。
     *
     * @param keys 一定不能为 {@literal null}.
     * @return key 被删除返回true
     * @see <a href="https://redis.io/commands/del">Redis Documentation: DEL</a>
     */
    public Long del(@NonNull String... keys) {
        return del(Arrays.stream(keys).toList());
    }

    /**
     * 删除给定的一个 key 或 多个key
     * 不存在的 key 会被忽略。
     *
     * @param keys 一定不能为 {@literal null}.
     * @return key 被删除返回true
     * @see <a href="https://redis.io/commands/del">Redis Documentation: DEL</a>
     */
    public Long del(@NonNull List<String> keys) {
        if (CollUtil.isEmpty(keys)) {
            return 0L;
        }
        List<List<String>> partitionKeys = Lists.partition(keys, BATCH_SIZE);
        long count = 0;
        for (List<String> list : partitionKeys) {
            Long delete = redisTemplate.delete(list);
            count += delete != null ? delete : 0L;
        }
        return count;
    }

    /**
     * 删除给定的一个 key 或 多个key
     * 不存在的 key 会被忽略。
     *
     * @param keys 一定不能为 {@literal null}.
     * @return key 被删除返回true
     * @see <a href="https://redis.io/commands/del">Redis Documentation: DEL</a>
     */
    public Long del(@NonNull Collection<CacheKey> keys) {
        return del(keys.stream().map(CacheKey::getKey).toList());
    }

    /**
     * 异步删除给定的一个 key 或 多个key
     * 不存在的 key 会被忽略。
     *
     * @param keys 一定不能为 {@literal null}.
     * @return key 被删除返回true
     * @see <a href="https://redis.io/commands/unlink">Redis Documentation: DEL</a>
     */
    public Long unlink(@NonNull CacheKey... keys) {
        return unlinkStrs(Arrays.stream(keys).map(CacheKey::getKey).toList());
    }

    /**
     * 异步删除给定的一个 key 或 多个key
     * 不存在的 key 会被忽略。
     *
     * @param keys 一定不能为 {@literal null}.
     * @return key 被删除返回true
     * @see <a href="https://redis.io/commands/unlink">Redis Documentation: DEL</a>
     */
    public Long unlink(@NonNull String... keys) {
        return unlinkStrs(Arrays.stream(keys).toList());
    }

    /**
     * 异步删除给定的一个 key 或 多个key
     * 不存在的 key 会被忽略。
     *
     * @param keys 一定不能为 {@literal null}.
     * @return key 被删除返回true
     * @see <a href="https://redis.io/commands/unlink">Redis Documentation: DEL</a>
     */
    public Long unlinkCacheKeys(@NonNull Collection<CacheKey> keys) {
        return unlinkStrs(keys.stream().map(CacheKey::getKey).toList());
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
     * 异步删除给定的一个 key 或 多个key
     * 不存在的 key 会被忽略。
     *
     * @param keys 一定不能为 {@literal null}.
     * @return key 被删除返回true
     * @see <a href="https://redis.io/commands/unlink">Redis Documentation: DEL</a>
     */
    public Long unlinkStrs(@NonNull List<String> keys) {
        if (CollUtil.isEmpty(keys)) {
            return 0L;
        }
        List<List<String>> partitionKeys = Lists.partition(keys, BATCH_SIZE);
        long count = 0;
        for (List<String> list : partitionKeys) {
            Long delete = redisTemplate.unlink(list);
            count += delete != null ? delete : 0L;
        }
        return count;
    }

    /**
     * 批量扫描后删除 匹配到的key
     *
     * @param pattern pattern
     * @date 2021/6/18 3:21 下午
     * @create [2021/6/18 3:21 下午 ] [tangyh] [初始创建]
     */
    public void scanUnlink(@NonNull String pattern) {
        log.info("pattern={}", pattern);
        if (StrUtil.isEmpty(pattern) || StrPool.STAR.equals(pattern.trim())) {
            throw BizException.wrap("必须指定匹配符");
        }
        List<String> keys = scan(pattern);
        log.info("keys={}", keys.size());
        if (CollUtil.isEmpty(keys)) {
            return;
        }
        unlinkStrs(keys);
    }

    /**
     * 查找所有符合给定模式 pattern 的 key 。
     * <p>
     * 例子：
     * KEYS * 匹配数据库中所有 key 。
     * KEYS h?llo 匹配 hello ， hallo 和 hxllo 等。
     * KEYS h*llo 匹配 hllo 和 heeeeello 等。
     * KEYS h[ae]llo 匹配 hello 和 hallo ，但不匹配 hillo 。
     * <p>
     * 特殊符号用 \ 隔开
     *
     * @param pattern 表达式
     * @return 符合给定模式的 key 列表
     * @see <a href="https://redis.io/commands/keys">Redis Documentation: KEYS</a>
     */
    public List<String> scan(@NonNull String pattern) {
        List<String> keyList = new ArrayList<>();
        scan(pattern, item -> {
            //符合条件的key
            Object key = redisTemplate.getKeySerializer().deserialize(item);
            if (ObjectUtil.isNotEmpty(key)) {
                keyList.add(String.valueOf(key));
            }
        });
        return keyList;
    }

    /**
     * scan 实现
     *
     * @param pattern  表达式
     * @param consumer 对迭代到的key进行操作
     */
    private void scan(String pattern, Consumer<byte[]> consumer) {
        redisTemplate.execute((RedisConnection connection) -> {
            try (Cursor<byte[]> cursor = connection.scan(ScanOptions.scanOptions().count(BATCH_SIZE).match(pattern).build())) {
                cursor.forEachRemaining(consumer);
                return null;
            }
        });
    }

    /**
     * 查找所有符合给定模式 pattern 的 key 。
     * <p>
     * 例子：
     * KEYS * 匹配数据库中所有 key 。
     * KEYS h?llo 匹配 hello ， hallo 和 hxllo 等。
     * KEYS h*llo 匹配 hllo 和 heeeeello 等。
     * KEYS h[ae]llo 匹配 hello 和 hallo ，但不匹配 hillo 。
     * <p>
     * 特殊符号用 \ 隔开
     *
     * @param pattern 表达式
     * @return 符合给定模式的 key 列表
     * @see <a href="https://redis.io/commands/keys">Redis Documentation: KEYS</a>
     */
    public Set<String> keys(@NonNull String pattern) {
        return redisTemplate.keys(pattern);
    }


    /**
     * 检查给定 key 是否存在。
     *
     * @param key 一定不能为 {@literal null}.
     * @return 是否存在
     * @see <a href="https://redis.io/commands/exists">Redis Documentation: EXISTS</a>
     */
    public Boolean exists(@NonNull String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 从当前数据库中随机返回(不删除)一个 key 。
     *
     * @return 当数据库不为空时，返回一个 key 。 当数据库为空时，返回 nil 。
     * @see <a href="https://redis.io/commands/randomkey">Redis Documentation: RANDOMKEY</a>
     */
    public String randomKey() {
        return redisTemplate.randomKey();
    }

    /**
     * 将 key 改名为 newkey 。
     * 当 key 和 newkey 相同，或者 key 不存在时，返回一个错误。
     * 当 newkey 已经存在时， RENAME 命令将覆盖旧值。
     *
     * @param oldKey 一定不能为 {@literal null}.
     * @param newKey 一定不能为 {@literal null}.
     * @see <a href="https://redis.io/commands/rename">Redis Documentation: RENAME</a>
     */
    public void rename(@NonNull String oldKey, @NonNull String newKey) {
        redisTemplate.rename(oldKey, newKey);
    }

    /**
     * 当且仅当 newkey 不存在时，将 key 改名为 newkey 。
     *
     * @param oldKey 一定不能为 {@literal null}.
     * @param newKey 一定不能为 {@literal null}.
     * @return 是否成功
     * @see <a href="https://redis.io/commands/renamenx">Redis Documentation: RENAMENX</a>
     */
    public Boolean renameNx(@NonNull String oldKey, String newKey) {
        return redisTemplate.renameIfAbsent(oldKey, newKey);
    }

    /**
     * 将当前数据库的 key 移动到给定的数据库 db 当中。
     * 如果当前数据库(源数据库)和给定数据库(目标数据库)有相同名字的给定 key ，或者 key 不存在于当前数据库，那么 MOVE 没有任何效果。
     * 因此，也可以利用这一特性，将 MOVE 当作锁(locking)原语(primitive)。
     *
     * @param key     一定不能为 {@literal null}.
     * @param dbIndex 数据库索引
     * @return 是否成功
     * @see <a href="https://redis.io/commands/move">Redis Documentation: MOVE</a>
     */
    public Boolean move(@NonNull String key, int dbIndex) {
        return redisTemplate.move(key, dbIndex);
    }

    /**
     * 为给定 key 设置生存时间，当 key 过期时(生存时间为 0 )，它会被自动删除。
     * 在 Redis 中，带有生存时间的 key 被称为『易失的』(volatile)。
     * <p>
     * 生存时间可以通过使用 DEL 命令来删除整个 key 来移除，或者被 SET 和 GETSET 命令覆写(overwrite)，这意味着，如果一个命令只是修改(alter)一个带生存时间的 key 的值而不是用一个新的 key 值来代替(replace)它的话，那么生存时间不会被改变。
     * <p>
     * 比如说，对一个 key 执行 INCR 命令，对一个列表进行 LPUSH 命令，或者对一个哈希表执行 HSET 命令，这类操作都不会修改 key 本身的生存时间。
     * <p>
     * 另一方面，如果使用 RENAME 对一个 key 进行改名，那么改名后的 key 的生存时间和改名前一样。
     * <p>
     * RENAME 命令的另一种可能是，尝试将一个带生存时间的 key 改名成另一个带生存时间的 another_key ，这时旧的 another_key (以及它的生存时间)会被删除，然后旧的 key 会改名为 another_key ，因此，新的 another_key 的生存时间也和原本的 key 一样。
     * <p>
     * 使用 PERSIST 命令可以在不删除 key 的情况下，移除 key 的生存时间，让 key 重新成为一个『持久的』(persistent) key 。
     *
     * @param key     一定不能为 {@literal null}.
     * @param seconds 过期时间 单位：秒
     * @return 是否成功
     * @see <a href="https://redis.io/commands/expire">Redis Documentation: EXPIRE</a>
     */
    public Boolean expire(@NonNull String key, long seconds) {
        ArgumentAssert.notEmpty(key, KEY_NOT_NULL);
        return redisTemplate.expire(key, seconds, TimeUnit.SECONDS);
    }

    /**
     * 为给定 key 设置生存时间，当 key 过期时(生存时间为 0 )，它会被自动删除。
     * 在 Redis 中，带有生存时间的 key 被称为『易失的』(volatile)。
     * <p>
     * 生存时间可以通过使用 DEL 命令来删除整个 key 来移除，或者被 SET 和 GETSET 命令覆写(overwrite)，这意味着，如果一个命令只是修改(alter)一个带生存时间的 key 的值而不是用一个新的 key 值来代替(replace)它的话，那么生存时间不会被改变。
     * <p>
     * 比如说，对一个 key 执行 INCR 命令，对一个列表进行 LPUSH 命令，或者对一个哈希表执行 HSET 命令，这类操作都不会修改 key 本身的生存时间。
     * <p>
     * 另一方面，如果使用 RENAME 对一个 key 进行改名，那么改名后的 key 的生存时间和改名前一样。
     * <p>
     * RENAME 命令的另一种可能是，尝试将一个带生存时间的 key 改名成另一个带生存时间的 another_key ，这时旧的 another_key (以及它的生存时间)会被删除，然后旧的 key 会改名为 another_key ，因此，新的 another_key 的生存时间也和原本的 key 一样。
     * <p>
     * 使用 PERSIST 命令可以在不删除 key 的情况下，移除 key 的生存时间，让 key 重新成为一个『持久的』(persistent) key 。
     *
     * @param key     一定不能为 {@literal null}.
     * @param timeout 过期时间
     * @return 是否成功
     * @see <a href="https://redis.io/commands/expire">Redis Documentation: EXPIRE</a>
     */
    public Boolean expire(@NonNull String key, @NonNull Duration timeout) {
        return expire(key, timeout.getSeconds());
    }

    /**
     * EXPIREAT 的作用和 EXPIRE 类似，都用于为 key 设置生存时间。不同在于 EXPIREAT 命令接受的时间参数是 UNIX 时间戳(unix timestamp)。
     *
     * @param key  一定不能为 {@literal null}.
     * @param date 过期时间
     * @return 是否成功
     * @see <a href="https://redis.io/commands/expireat">Redis Documentation: EXPIREAT</a>
     */
    public Boolean expireAt(@NonNull String key, @NonNull Date date) {
        return redisTemplate.expireAt(key, date);
    }

    /**
     * EXPIREAT 的作用和 EXPIRE 类似，都用于为 key 设置生存时间。不同在于 EXPIREAT 命令接受的时间参数是 UNIX 时间戳(unix timestamp)。
     *
     * @param key           一定不能为 {@literal null}.
     * @param unixTimestamp 过期时间戳
     * @return 是否成功
     * @see <a href="https://redis.io/commands/expireat">Redis Documentation: EXPIREAT</a>
     */
    public Boolean expireAt(@NonNull String key, long unixTimestamp) {
        return expireAt(key, new Date(unixTimestamp));
    }

    /**
     * 这个命令和 EXPIRE 命令的作用类似，但是它以毫秒为单位设置 key 的生存时间，而不像 EXPIRE 命令那样，以秒为单位。
     *
     * @param key          一定不能为 {@literal null}.
     * @param milliseconds 过期时间 单位： 毫米
     * @return 是否成功
     * @see <a href="https://redis.io/commands/pexpire">Redis Documentation: PEXPIRE</a>
     */
    public Boolean pExpire(@NonNull String key, long milliseconds) {
        return redisTemplate.expire(key, milliseconds, TimeUnit.MILLISECONDS);
    }

    /**
     * 移除给定 key 的生存时间，将这个 key 从『易失的』(带生存时间 key )转换成『持久的』(一个不带生存时间、永不过期的 key )。
     *
     * @param key 一定不能为 {@literal null}.
     * @return 是否成功
     * @see <a href="https://redis.io/commands/persist">Redis Documentation: PERSIST</a>
     */
    public Boolean persist(@NonNull String key) {
        return redisTemplate.persist(key);
    }

    /**
     * 返回 key 所储存的值的类型。
     *
     * @param key 一定不能为 {@literal null}.
     * @return none (key不存在)、string (字符串)、list (列表)、set (集合)、zset (有序集)、hash (哈希表) 、stream （流）
     * @see <a href="https://redis.io/commands/type">Redis Documentation: TYPE</a>
     */
    public String type(@NonNull String key) {
        DataType type = redisTemplate.type(key);
        return type == null ? DataType.NONE.code() : type.code();
    }

    /**
     * 以秒为单位，返回给定 key 的剩余生存时间(TTL, time to live)。
     *
     * @param key 一定不能为 {@literal null}.
     * @return 当 key 不存在时，返回 -2 。 当 key 存在但没有设置剩余生存时间时，返回 -1 。 否则，以秒为单位，返回 key 的剩余生存时间。
     * @see <a href="https://redis.io/commands/ttl">Redis Documentation: TTL</a>
     */
    public Long ttl(@NonNull String key) {
        return redisTemplate.getExpire(key);
    }

    /**
     * 这个命令类似于 TTL 命令，但它以毫秒为单位返回 key 的剩余生存时间，而不是像 TTL 命令那样，以秒为单位。
     *
     * @param key 一定不能为 {@literal null}.
     * @return 当 key 不存在时，返回 -2 。当 key 存在但没有设置剩余生存时间时，返回 -1 。否则，以毫秒为单位，返回 key 的剩余生存时间
     * @see <a href="https://redis.io/commands/pttl">Redis Documentation: PTTL</a>
     */
    public Long pTtl(@NonNull String key) {
        return redisTemplate.getExpire(key, TimeUnit.MILLISECONDS);
    }
    // ---------------------------- common end ----------------------------

    // ---------------------------- string start ----------------------------

    /**
     * 将字符串值 value 存放到 key 。
     * <p>
     * 如果 key 已经持有其他值， SET 就覆写旧值， 无视类型。
     * 当 SET 命令对一个带有生存时间（TTL）的键进行设置之后， 该键原有的 TTL 将被清除.
     *
     * @param key             一定不能为 {@literal null}.
     * @param value           值
     * @param cacheNullValues 是否缓存null对象
     * @see <a href="https://redis.io/commands/set">Redis Documentation: SET</a>
     */
    public void set(@NonNull String key, Object value, boolean... cacheNullValues) {
        ArgumentAssert.notNull(key, KEY_NOT_NULL);
        boolean cacheNullVal = cacheNullValues.length > 0 ? cacheNullValues[0] : defaultCacheNullVal;
        if (!cacheNullVal && value == null) {
            return;
        }

        valueOps.set(key, value == null ? newNullVal() : value);
    }

	public void set(@NonNull String key, Object value) {
		stringRedisTemplate.opsForValue().set(key, JsonUtils.toStr(value));
	}

    /**
     * 设置缓存
     *
     * @param cacheKey        缓存key 一定不能为 {@literal null}.
     * @param value           缓存value
     * @param cacheNullValues 是否缓存null对象
     */
    public void set(@NonNull CacheKey cacheKey, Object value, boolean... cacheNullValues) {
        boolean cacheNullVal = cacheNullValues.length > 0 ? cacheNullValues[0] : defaultCacheNullVal;
        ArgumentAssert.notNull(cacheKey, CACHE_KEY_NOT_NULL);
        String key = cacheKey.getKey();
        Duration expire = cacheKey.getExpire();
        if (expire == null) {
            set(key, value, cacheNullVal);
        } else {
            setEx(key, value, expire, cacheNullVal);
        }
    }

    /**
     * 将键 key 的值设置为 value ， 并将键 key 的生存时间设置为 seconds 秒钟。
     * 如果键 key 已经存在， 那么 SETEX 命令将覆盖已有的值。
     * <p>
     * SETEX 命令的效果和以下两个命令的效果类似：
     * SET key value
     * EXPIRE key seconds  # 设置生存时间
     * <p>
     * SETEX 和这两个命令的不同之处在于 SETEX 是一个原子（atomic）操作， 它可以在同一时间内完成设置值和设置过期时间这两个操作， 因此 SETEX 命令在储存缓存的时候非常实用。
     *
     * @param key             一定不能为 {@literal null}.
     * @param value           值
     * @param timeout         一定不能为 {@literal null}.
     * @param cacheNullValues 是否缓存null对象
     * @throws IllegalArgumentException if either {@code key}, {@code value} or {@code timeout} is not present.
     * @see <a href="https://redis.io/commands/setex">Redis Documentation: SETEX</a>
     */
    public void setEx(@NonNull String key, Object value, Duration timeout, boolean... cacheNullValues) {
        ArgumentAssert.notNull(key, KEY_NOT_NULL);
        boolean cacheNullVal = cacheNullValues.length > 0 ? cacheNullValues[0] : defaultCacheNullVal;
        if (!cacheNullVal && value == null) {
            return;
        }
        valueOps.set(key, value == null ? newNullVal() : value, timeout);
    }

    /**
     * 将键 key 的值设置为 value ， 并将键 key 的生存时间设置为 seconds 秒钟。
     * 如果键 key 已经存在， 那么 SETEX 命令将覆盖已有的值。
     * <p>
     * SETEX 命令的效果和以下两个命令的效果类似：
     * SET key value
     * EXPIRE key seconds  # 设置生存时间
     * <p>
     * SETEX 和这两个命令的不同之处在于 SETEX 是一个原子（atomic）操作， 它可以在同一时间内完成设置值和设置过期时间这两个操作， 因此 SETEX 命令在储存缓存的时候非常实用。
     *
     * @param key             一定不能为 {@literal null}.
     * @param value           值
     * @param cacheNullValues 是否缓存null对象
     * @param seconds         过期时间 单位秒
     * @see <a href="https://redis.io/commands/setex">Redis Documentation: SETEX</a>
     */
    public void setEx(@NonNull String key, Object value, long seconds, boolean... cacheNullValues) {
        setEx(key, value, Duration.ofSeconds(seconds), cacheNullValues);
    }

    /**
     * 如果存在key，则设置key以保存字符串值。
     *
     * @param key   一定不能为 {@literal null}.
     * @param value 一定不能为 {@literal null}.
     * @return 设置成功返回true。
     * @throws IllegalArgumentException 如果{@code key} 或 {@code value} 不存在
     * @see <a href="https://redis.io/commands/set">Redis Documentation: SET</a>
     * @since 2.1
     */
    @Nullable
    public Boolean setXx(@NonNull String key, String value, boolean... cacheNullValues) {
        boolean cacheNullVal = cacheNullValues.length > 0 ? cacheNullValues[0] : defaultCacheNullVal;
        return valueOps.setIfPresent(key, cacheNullVal && value == null ? newNullVal() : value);
    }

    /**
     * 如果存在key，则设置key以保存字符串值。
     *
     * @param key     一定不能为 {@literal null}.
     * @param value   一定不能为 {@literal null}.
     * @param seconds 过期时间 单位秒
     * @return 设置成功返回true。
     * @throws IllegalArgumentException 如果{@code key} 或 {@code value} 不存在
     * @see <a href="https://redis.io/commands/set">Redis Documentation: SET</a>
     * @since 2.1
     */
    @Nullable
    public Boolean setXx(@NonNull String key, String value, long seconds, boolean... cacheNullValues) {
        boolean cacheNullVal = cacheNullValues.length > 0 ? cacheNullValues[0] : defaultCacheNullVal;
        return valueOps.setIfPresent(key, cacheNullVal && value == null ? newNullVal() : value, seconds, TimeUnit.SECONDS);
    }

    /**
     * 如果存在key，则设置key以保存字符串值。
     *
     * @param key     一定不能为 {@literal null}.
     * @param value   一定不能为 {@literal null}.
     * @param timeout 一定不能为 {@literal null}.
     * @return 设置成功返回true。
     * @throws IllegalArgumentException 如果{@code key} 或 {@code value} 不存在
     * @see <a href="https://redis.io/commands/set">Redis Documentation: SET</a>
     * @since 2.1
     */
    @Nullable
    public Boolean setXx(@NonNull String key, String value, Duration timeout, boolean... cacheNullValues) {
        boolean cacheNullVal = cacheNullValues.length > 0 ? cacheNullValues[0] : defaultCacheNullVal;
        return valueOps.setIfPresent(key, cacheNullVal && value == null ? newNullVal() : value, timeout);
    }

    /**
     * 只在键 key 不存在的情况下， 将键 key 的值设置为 value 。
     * <p>
     * 若键 key 已经存在， 则 SETNX 命令不做任何动作。
     * <p>
     * SETNX 是『SET if Not eXists』(如果不存在，则 SET)的简写。
     *
     * @param key   一定不能为 {@literal null}.
     * @param value 一定不能为 {@literal null}.
     * @return 设置成功返回true
     * @see <a href="https://redis.io/commands/setnx">Redis Documentation: SETNX</a>
     */
    @Nullable
    public Boolean setNx(@NonNull String key, String value, boolean... cacheNullValues) {
        boolean cacheNullVal = cacheNullValues.length > 0 ? cacheNullValues[0] : defaultCacheNullVal;
        return valueOps.setIfAbsent(key, cacheNullVal && value == null ? newNullVal() : value);
    }

    /**
     * 返回键 key 储存的字符串值的长度
     *
     * @param key 一定不能为 {@literal null}.
     * @return 字符串值的长度。 当键 key 不存在时， 命令返回 0
     * @see <a href="https://redis.io/commands/strlen">Redis Documentation: STRLEN</a>
     */
    @Nullable
    public Long strLen(@NonNull String key) {
        return valueOps.size(key);
    }

    /**
     * 如果键 key 已经存在并且它的值是一个字符串， APPEND 命令将把 value 追加到键 key 现有值的末尾。
     * 如果 key 不存在， APPEND 就简单地将键 key 的值设为 value ， 就像执行 SET key value 一样。
     *
     * @param key 一定不能为 {@literal null}.
     * @return 追加 value 之后， 键 key 的值的长度
     * @see <a href="https://redis.io/commands/append">Redis Documentation: APPEND</a>
     */
    @Nullable
    public Integer append(@NonNull String key, String value) {
        return valueOps.append(key, value);
    }

    /**
     * 从偏移量 offset 开始， 用 value 参数覆写(overwrite)键 key 储存的字符串值。
     * <p>
     * 不存在的键 key 当作空白字符串处理。
     * <p>
     * SETRANGE 命令会确保字符串足够长以便将 value 设置到指定的偏移量上， 如果键 key 原来储存的字符串长度比偏移量小(比如字符串只有 5 个字符长，但你设置的 offset 是 10 )， 那么原字符和偏移量之间的空白将用零字节(zerobytes, "\x00" )进行填充。
     * <p>
     * 因为 Redis 字符串的大小被限制在 512 兆(megabytes)以内， 所以用户能够使用的最大偏移量为 2^29-1(536870911) ， 如果你需要使用比这更大的空间， 请使用多个 key 。
     *
     * @param key    一定不能为 {@literal null}.
     * @param value  字符串
     * @param offset 最大不能超过 536870911
     * @see <a href="https://redis.io/commands/setrange">Redis Documentation: SETRANGE</a>
     */
    public void setRange(@NonNull String key, String value, long offset) {
        //TODO 序列化hug
        valueOps.set(key, value, offset);
    }

    /**
     * 返回键 key 储存的字符串值的指定部分， 字符串的截取范围由 start 和 end 两个偏移量决定 (包括 start 和 end 在内)。
     * 负数偏移量表示从字符串的末尾开始计数， -1 表示最后一个字符， -2 表示倒数第二个字符， 以此类推。
     * GETRANGE 通过保证子字符串的值域(range)不超过实际字符串的值域来处理超出范围的值域请求。
     *
     * @param key   一定不能为 {@literal null}.
     * @param start 开始偏移量
     * @param end   结束偏移量
     * @return GETRANGE 命令会返回字符串值的指定部分。
     * @see <a href="https://redis.io/commands/getrange">Redis Documentation: GETRANGE</a>
     */
    public String getRange(@NonNull String key, long start, long end) {
        //TODO 序列化hug
        return valueOps.get(key, start, end);
    }

    private Map<String, Object> mSetMap(@NonNull Map<String, Object> map, boolean cacheNullVal) {
        Map<String, Object> mSetMap = new HashMap<>(CollHelper.initialCapacity(map.size()));
        map.forEach((k, v) -> {
            if (v == null && cacheNullVal) {
                mSetMap.put(k, newNullVal());
            } else {
                mSetMap.put(k, v);
            }
        });
        return mSetMap;
    }

    /**
     * 同时为一个或多个键设置值。
     * 如果某个给定键已经存在， 那么 MSET 将使用新值去覆盖旧值， 如果这不是你所希望的效果， 请考虑使用 MSETNX 命令， 这个命令只会在所有给定键都不存在的情况下进行设置。
     * MSET 是一个原子性(atomic)操作， 所有给定键都会在同一时间内被设置， 不会出现某些键被设置了但是另一些键没有被设置的情况。
     *
     * @param map          一定不能为 {@literal null}.
     * @param cacheNullVal 是否缓存空值
     * @see <a href="https://redis.io/commands/mset">Redis Documentation: MSET</a>
     */
    public void mSet(@NonNull Map<String, Object> map, boolean cacheNullVal) {
        Map<String, Object> mSetMap = mSetMap(map, cacheNullVal);

        valueOps.multiSet(mSetMap);
    }

	public <T> void mSet(Map<String, T> map, long time) {
		Map<String, T> collect = map.entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey(), (e) -> e.getValue()));
		valueOps.multiSet(collect);
		if (time > 0) {
			map.forEach((key, value) -> expire(key, time));
		}
	}

    /**
     * 同时为一个或多个键设置值。
     * 如果某个给定键已经存在， 那么 MSET 将使用新值去覆盖旧值， 如果这不是你所希望的效果， 请考虑使用 MSETNX 命令， 这个命令只会在所有给定键都不存在的情况下进行设置。
     * MSET 是一个原子性(atomic)操作， 所有给定键都会在同一时间内被设置， 不会出现某些键被设置了但是另一些键没有被设置的情况。
     *
     * @param map 一定不能为 {@literal null}.
     * @see <a href="https://redis.io/commands/mset">Redis Documentation: MSET</a>
     */
    public void mSet(@NonNull Map<String, Object> map) {
        mSet(map, defaultCacheNullVal);
    }

    /**
     * 当且仅当所有给定键都不存在时， 为所有给定键设置值。
     * 即使只有一个给定键已经存在， MSETNX 命令也会拒绝执行对所有键的设置操作。
     * MSETNX 是一个原子性(atomic)操作， 所有给定键要么就全部都被设置， 要么就全部都不设置， 不可能出现第三种状态。
     *
     * @param map          一定不能为 {@literal null}.
     * @param cacheNullVal 是否缓存空值
     * @see <a href="https://redis.io/commands/msetnx">Redis Documentation: MSET</a>
     */
    public void mSetNx(@NonNull Map<String, Object> map, boolean cacheNullVal) {
        Map<String, Object> mSetMap = mSetMap(map, cacheNullVal);

        valueOps.multiSetIfAbsent(mSetMap);
    }

    /**
     * 当且仅当所有给定键都不存在时， 为所有给定键设置值。
     * 即使只有一个给定键已经存在， MSETNX 命令也会拒绝执行对所有键的设置操作。
     * MSETNX 是一个原子性(atomic)操作， 所有给定键要么就全部都被设置， 要么就全部都不设置， 不可能出现第三种状态。
     *
     * @param map 一定不能为 {@literal null}.
     * @see <a href="https://redis.io/commands/msetnx">Redis Documentation: MSET</a>
     */
    public void mSetNx(@NonNull Map<String, Object> map) {
        mSetNx(map, defaultCacheNullVal);
    }


    /**
     * 为键 key 储存的数字值加上一。
     * 如果键 key 不存在， 那么它的值会先被初始化为 0 ， 然后再执行 INCR 命令。
     * 如果键 key 储存的值不能被解释为数字， 那么 INCR 命令将返回一个错误。
     * 本操作的值限制在 64 位(bit)有符号数字表示之内。
     * <p>
     * 提示： INCR 命令是一个针对字符串的操作。 因为 Redis 并没有专用的整数类型， 所以键 key 储存的值在执行 INCR 命令时会被解释为十进制 64 位有符号整数。
     *
     * @param key 一定不能为 {@literal null}.
     * @return 返回键 key 在执行加一操作之后的值。
     * @see <a href="https://redis.io/commands/incr">Redis Documentation: INCR</a>
     */
    public Long incr(@NonNull CacheKey key) {
        Long increment = stringRedisTemplate.opsForValue().increment(key.getKey());
        setExpire(key);
        return increment;
    }

	private static final String LUA_INCR_EXPIRE =
			"local key,ttl=KEYS[1],ARGV[1] \n" +
					" \n" +
					"if redis.call('EXISTS',key)==0 then   \n" +
					"  redis.call('SETEX',key,ttl,1) \n" +
					"  return 1 \n" +
					"else \n" +
					"  return tonumber(redis.call('INCR',key)) \n" +
					"end ";

	public Long inc(String key, int time, TimeUnit unit) {
		RedisScript<Long> redisScript = new DefaultRedisScript<>(LUA_INCR_EXPIRE, Long.class);
		return stringRedisTemplate.execute(redisScript, Collections.singletonList(key), String.valueOf(unit.toSeconds(time)));
	}

    /**
     * 为键 key 储存的数字值加上增量 increment 。
     * 如果键 key 不存在， 那么键 key 的值会先被初始化为 0 ， 然后再执行 INCRBY 命令。
     * 如果键 key 储存的值不能被解释为数字， 那么 INCRBY 命令将返回一个错误。
     * 本操作的值限制在 64 位(bit)有符号数字表示之内。
     * 关于递增(increment) / 递减(decrement)操作的更多信息， 请参见 INCR 命令的文档。
     *
     * @param key       一定不能为 {@literal null}.
     * @param increment 增量值
     * @return 返回键 key 在执行加上 increment ，操作之后的值。
     * @see <a href="https://redis.io/commands/incrby">Redis Documentation: INCRBY</a>
     */
    public Long incrBy(@NonNull CacheKey key, long increment) {
        Long incrBy = stringRedisTemplate.opsForValue().increment(key.getKey(), increment);
        setExpire(key);
        return incrBy;
    }

    /**
     * 为键 key 储存的值加上浮点数增量 increment 。
     * 如果键 key 不存在， 那么 INCRBYFLOAT 会先将键 key 的值设为 0 ， 然后再执行加法操作。
     * 如果命令执行成功， 那么键 key 的值会被更新为执行加法计算之后的新值， 并且新值会以字符串的形式返回给调用者。
     * <p>
     * 无论是键 key 的值还是增量 increment ， 都可以使用像 2.0e7 、 3e5 、 90e-2 那样的指数符号(exponential notation)来表示， 但是， 执行 INCRBYFLOAT 命令之后的值总是以同样的形式储存， 也即是， 它们总是由一个数字， 一个（可选的）小数点和一个任意长度的小数部分组成（比如 3.14 、 69.768 ，诸如此类)， 小数部分尾随的 0 会被移除， 如果可能的话， 命令还会将浮点数转换为整数（比如 3.0 会被保存成 3 ）。
     * 此外， 无论加法计算所得的浮点数的实际精度有多长， INCRBYFLOAT 命令的计算结果最多只保留小数点的后十七位。
     * <p>
     * 当以下任意一个条件发生时， 命令返回一个错误：
     * 键 key 的值不是字符串类型(因为 Redis 中的数字和浮点数都以字符串的形式保存，所以它们都属于字符串类型）；
     * 键 key 当前的值或者给定的增量 increment 不能被解释(parse)为双精度浮点数。
     *
     * @param key       一定不能为 {@literal null}.
     * @param increment 增量值
     * @return 在加上增量 increment 之后， 键 key 的值。
     * @see <a href="https://redis.io/commands/incrbyfloat">Redis Documentation: INCRBYFLOAT</a>
     */
    public Double incrByFloat(@NonNull CacheKey key, double increment) {
        Double incrByFloat = stringRedisTemplate.opsForValue().increment(key.getKey(), increment);
        setExpire(key);
        return incrByFloat;
    }

    /**
     * 获取key中存放的Long值
     *
     * @param key 一定不能为 {@literal null}.
     * @return key中存储的的数字
     */
    public Long getCounter(@NonNull CacheKey key, Long... defaultValue) {
        Object val = stringRedisTemplate.opsForValue().get(key.getKey());
        if (isNullVal(val)) {
            return defaultValue.length > 0 ? defaultValue[0] : null;
        }
        return Convert.toLong(val);
    }

    /**
     * 获取key中存放的Long值
     *
     * @param key 一定不能为 {@literal null}.
     * @return key中存储的的数字
     */
    public Long getCounter(@NonNull CacheKey key, Function<CacheKey, Long> loader) {
        Object val = stringRedisTemplate.opsForValue().get(key.getKey());
        if (isNullVal(val)) {
            return loader.apply(key);
        }
        return Convert.toLong(val);
    }

    /**
     * 为键 key 储存的数字值减去一。
     * 如果键 key 不存在， 那么键 key 的值会先被初始化为 0 ， 然后再执行 DECR 操作。
     * 如果键 key 储存的值不能被解释为数字， 那么 DECR 命令将返回一个错误。
     * 本操作的值限制在 64 位(bit)有符号数字表示之内。
     *
     * @param key 一定不能为 {@literal null}.
     * @return 在减去增量 1 之后， 键 key 的值。
     * @see <a href="https://redis.io/commands/decr">Redis Documentation: DECR</a>
     */
    public Long decr(@NonNull CacheKey key) {
        Long decr = stringRedisTemplate.opsForValue().decrement(key.getKey());
        setExpire(key);
        return decr;
    }

    /**
     * 将 key 所储存的值减去减量 decrement 。
     * 如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 DECRBY 操作。
     * 如果值包含错误的类型，或字符串类型的值不能表示为数字，那么返回一个错误。
     * 本操作的值限制在 64 位(bit)有符号数字表示之内。
     *
     * @param key 一定不能为 {@literal null}.
     * @return 在减去增量 decrement 之后， 键 key 的值。
     * @see <a href="https://redis.io/commands/decr">Redis Documentation: DECR</a>
     */
    public Long decrBy(@NonNull CacheKey key, long decrement) {
        Long decr = stringRedisTemplate.opsForValue().decrement(key.getKey(), decrement);
        setExpire(key);
        return decr;
    }
    // ---------------------------- string end ----------------------------


    // ---------------------------- hash start ----------------------------

    /**
     * 将哈希表 key 中的域 field 的值设为 value 。
     * 如果 key 不存在，一个新的哈希表被创建并进行 HSET 操作。
     * 如果域 field 已经存在于哈希表中，旧值将被覆盖。
     *
     * @param key             一定不能为 {@literal null}.
     * @param field           一定不能为 {@literal null}.
     * @param cacheNullValues 是否缓存空对象
     * @param value           值
     * @see <a href="https://redis.io/commands/hset">Redis Documentation: HSET</a>
     */
    public void hSet(@NonNull String key, @NonNull Object field, Object value, boolean... cacheNullValues) {
        ArgumentAssert.notEmpty(key, KEY_NOT_NULL);
        ArgumentAssert.notNull(field, "field不能为空");
        boolean cacheNullVal = cacheNullValues.length > 0 ? cacheNullValues[0] : defaultCacheNullVal;

        if (!cacheNullVal && value == null) {
            return;
        }
        hashOps.put(key, field, value == null ? newNullVal() : value);
    }

    /**
     * 将哈希表 key 中的域 field 的值设为 value 。
     * 如果 key 不存在，一个新的哈希表被创建并进行 HSET 操作。
     * 如果域 field 已经存在于哈希表中，旧值将被覆盖。
     *
     * @param key             一定不能为 {@literal null}.
     * @param value           值
     * @param cacheNullValues 是否缓存空对象
     * @see <a href="https://redis.io/commands/hset">Redis Documentation: HSET</a>
     */
    public void hSet(@NonNull CacheHashKey key, Object value, boolean... cacheNullValues) {
        ArgumentAssert.notNull(key, "CacheHashKey不能为空");

        this.hSet(key.getKey(), key.getField(), value, cacheNullValues);
        setExpire(key);
    }

    /**
     * 检查给定域 field 是否存在于哈希表 hash 当中
     *
     * @param key   一定不能为 {@literal null}.
     * @param field 一定不能为 {@literal null}.
     * @return 是否存在
     * @see <a href="https://redis.io/commands/hexists">Redis Documentation: HEXISTS</a>
     */
    public Boolean hExists(@NonNull String key, @NonNull Object field) {
        return hashOps.hasKey(key, field);
    }

    /**
     * 检查给定域 field 是否存在于哈希表 hash 当中
     *
     * @param cacheHashKey 一定不能为 {@literal null}.
     * @return 是否存在
     * @see <a href="https://redis.io/commands/hexists">Redis Documentation: HEXISTS</a>
     */
    public Boolean hExists(@NonNull CacheHashKey cacheHashKey) {
        return hashOps.hasKey(cacheHashKey.getKey(), cacheHashKey.getField());
    }

    /**
     * 删除哈希表 key 中的一个或多个指定域，不存在的域将被忽略。
     *
     * @param key    一定不能为 {@literal null}.
     * @param fields 一定不能为 {@literal null}.
     * @return 删除的数量
     * @see <a href="https://redis.io/commands/hdel">Redis Documentation: HDEL</a>
     */
    public Long hDel(@NonNull String key, Object... fields) {
        return hashOps.delete(key, fields);
    }

    public Long hDel(@NonNull CacheHashKey key) {
        return hashOps.delete(key.getKey(), key.getField());
    }


    /**
     * 返回哈希表 key 中域的数量。
     *
     * @param key 一定不能为 {@literal null}.
     * @return 哈希表中域的数量。
     * @see <a href="https://redis.io/commands/hlen">Redis Documentation: HLEN</a>
     */
    public Long hLen(@NonNull String key) {
        return hashOps.size(key);
    }

    /**
     * 返回哈希表 key 中， 与给定域 field 相关联的值的字符串长度
     *
     * @param key   一定不能为 {@literal null}.
     * @param field 一定不能为 {@literal null}.
     * @return 返回哈希表 key 中， 与给定域 field 相关联的值的字符串长度。
     * @see <a href="https://redis.io/commands/hstrlen">Redis Documentation: HSTRLEN</a>
     */
    public Long hStrLen(@NonNull String key, @NonNull Object field) {
        return hashOps.lengthOfValue(key, field);
    }

    /**
     * 为哈希表 key 中的域 field 的值加上增量 increment 。
     * 增量也可以为负数，相当于对给定域进行减法操作。
     * 如果 key 不存在，一个新的哈希表被创建并执行 HINCRBY 命令。
     * 如果域 field 不存在，那么在执行命令前，域的值被初始化为 0 。
     * 对一个储存字符串值的域 field 执行 HINCRBY 命令将造成一个错误。
     * 本操作的值被限制在 64 位(bit)有符号数字表示之内。
     *
     * @param key       一定不能为 {@literal null}.
     * @param increment 增量
     * @return 执行 HINCRBY 命令之后，哈希表 key 中域 field 的值
     * @see <a href="https://redis.io/commands/hincrby">Redis Documentation: HINCRBY</a>
     */
    public Long hIncrBy(@NonNull CacheHashKey key, long increment) {
        Long hIncrBy = stringRedisTemplate.opsForHash().increment(key.getKey(), key.getField(), increment);
        if (key.getExpire() != null) {
            stringRedisTemplate.expire(key.getKey(), key.getExpire());
        }
        return hIncrBy;
    }

    /**
     * 为哈希表 key 中的域 field 加上浮点数增量 increment 。
     * 如果哈希表中没有域 field ，那么 HINCRBYFLOAT 会先将域 field 的值设为 0 ，然后再执行加法操作。
     * 如果键 key 不存在，那么 HINCRBYFLOAT 会先创建一个哈希表，再创建域 field ，最后再执行加法操作。
     * 当以下任意一个条件发生时，返回一个错误：
     * 1:域 field 的值不是字符串类型(因为 redis 中的数字和浮点数都以字符串的形式保存，所以它们都属于字符串类型）
     * 2:域 field 当前的值或给定的增量 increment 不能解释(parse)为双精度浮点数(double precision floating point number)
     * HINCRBYFLOAT 命令的详细功能和 INCRBYFLOAT 命令类似，请查看 INCRBYFLOAT 命令获取更多相关信息。
     *
     * @param key       一定不能为 {@literal null}.
     * @param increment 增量
     * @return 执行 HINCRBY 命令之后，哈希表 key 中域 field 的值
     * @see <a href="https://redis.io/commands/hincrbyfloat">Redis Documentation: HINCRBYFLOAT</a>
     */
    public Double hIncrByFloat(@NonNull CacheHashKey key, double increment) {
        Double hIncrBy = stringRedisTemplate.opsForHash().increment(key.getKey(), key.getField(), increment);
        if (key.getExpire() != null) {
            stringRedisTemplate.expire(key.getKey(), key.getExpire());
        }
        return hIncrBy;
    }

    /**
     * 同时将多个 field-value (域-值)对设置到哈希表 key 中。
     * 此命令会覆盖哈希表中已存在的域。
     * 如果 key 不存在，一个空哈希表被创建并执行 HMSET 操作。
     *
     * @param key             一定不能为 {@literal null}.
     * @param hash            一定不能为 {@literal null}.
     * @param cacheNullValues 是否缓存空值
     * @see <a href="https://redis.io/commands/hmset">Redis Documentation: hmset</a>
     */
    public <K, V> void hmSet(@NonNull String key, @NonNull Map<K, V> hash, boolean... cacheNullValues) {
        boolean cacheNullVal = cacheNullValues.length > 0 ? cacheNullValues[0] : defaultCacheNullVal;
        Map<Object, Object> newMap = new HashMap<>(CollHelper.initialCapacity(hash.size()));

        hash.forEach((k, v) -> {
            if (v == null && cacheNullVal) {
                newMap.put(k, newNullVal());
            } else {
                newMap.put(k, v);
            }
        });

        hashOps.putAll(key, newMap);
    }


    /**
     * 返回哈希表 key 中的所有域。
     *
     * @param key 一定不能为 {@literal null}.
     * @return 所有的 filed
     * @see <a href="https://redis.io/commands/hkeys">Redis Documentation: hkeys</a>
     */
    public <HK> Set<HK> hKeys(@NonNull String key) {
        return (Set<HK>) hashOps.keys(key);
    }
    // ---------------------------- hash end ----------------------------


    // ---------------------------- list start ----------------------------

    /**
     * 将一个或多个值 value 插入到列表 key 的表头
     * 如果有多个 value 值，那么各个 value 值按从左到右的顺序依次插入到表头： 比如说，对空列表 mylist 执行命令 LPUSH mylist a b c ，列表的值将是 c b a ，这等同于原子性地执行 LPUSH mylist a 、 LPUSH mylist b 和 LPUSH mylist c 三个命令。
     * 如果 key 不存在，一个空列表会被创建并执行 LPUSH 操作。
     * 当 key 存在但不是列表类型时，返回一个错误。
     *
     * @param key    一定不能为 {@literal null}.
     * @param values 值
     * @return 返回列表的长度
     * @see <a href="https://redis.io/commands/lpush">Redis Documentation: LPUSH</a>
     */
    @Nullable
    public Long lPush(@NonNull String key, Object... values) {
        return listOps.leftPushAll(key, values);
    }

    /**
     * 将一个或多个值 value 插入到列表 key 的表头
     * 如果有多个 value 值，那么各个 value 值按从左到右的顺序依次插入到表头： 比如说，对空列表 mylist 执行命令 LPUSH mylist a b c ，列表的值将是 c b a ，这等同于原子性地执行 LPUSH mylist a 、 LPUSH mylist b 和 LPUSH mylist c 三个命令。
     * 如果 key 不存在，一个空列表会被创建并执行 LPUSH 操作。
     * 当 key 存在但不是列表类型时，返回一个错误。
     *
     * @param key    一定不能为 {@literal null}.
     * @param values 值
     * @return 返回列表的长度
     * @see <a href="https://redis.io/commands/lpush">Redis Documentation: LPUSH</a>
     */
    @Nullable
    public Long lPush(@NonNull String key, Collection<Object> values) {
        return listOps.leftPushAll(key, values);
    }

    /**
     * 将值 value 插入到列表 key 的表头，当且仅当 key 存在并且是一个列表。
     * 和 LPUSH key value [value …] 命令相反，当 key 不存在时， LPUSHX 命令什么也不做
     *
     * @param key    一定不能为 {@literal null}.
     * @param values 值
     * @return 返回列表的长度
     * @see <a href="https://redis.io/commands/lpushx">Redis Documentation: LPUSHX</a>
     */
    @Nullable
    public Long lPushX(@NonNull String key, Object values) {
        return listOps.leftPushIfPresent(key, values);
    }

    /**
     * 将一个或多个值 value 插入到列表 key 的表尾(最右边)。
     * 如果有多个 value 值，那么各个 value 值按从左到右的顺序依次插入到表尾：比如对一个空列表 mylist 执行 RPUSH mylist a b c ，得出的结果列表为 a b c ，等同于执行命令 RPUSH mylist a 、 RPUSH mylist b 、 RPUSH mylist c 。
     * 如果 key 不存在，一个空列表会被创建并执行 RPUSH 操作。
     * 当 key 存在但不是列表类型时，返回一个错误。
     *
     * @param key    一定不能为 {@literal null}.
     * @param values 值
     * @return 返回列表的长度
     * @see <a href="https://redis.io/commands/rpush">Redis Documentation: RPUSH</a>
     */
    @Nullable
    public Long rPush(@NonNull String key, Object... values) {
        return listOps.rightPushAll(key, values);
    }

    /**
     * 将一个或多个值 value 插入到列表 key 的表尾(最右边)。
     * 如果有多个 value 值，那么各个 value 值按从左到右的顺序依次插入到表尾：比如对一个空列表 mylist 执行 RPUSH mylist a b c ，得出的结果列表为 a b c ，等同于执行命令 RPUSH mylist a 、 RPUSH mylist b 、 RPUSH mylist c 。
     * 如果 key 不存在，一个空列表会被创建并执行 RPUSH 操作。
     * 当 key 存在但不是列表类型时，返回一个错误。
     *
     * @param key    一定不能为 {@literal null}.
     * @param values 值
     * @return 返回列表的长度
     * @see <a href="https://redis.io/commands/rpush">Redis Documentation: RPUSH</a>
     */
    @Nullable
    public Long rPush(@NonNull String key, Collection<Object> values) {
        return listOps.rightPushAll(key, values);
    }

    /**
     * 将值 value 插入到列表 key 的表尾，当且仅当 key 存在并且是一个列表。
     * <p>
     * 和 RPUSH key value [value …] 命令相反，当 key 不存在时， RPUSHX 命令什么也不做。
     *
     * @param key   一定不能为 {@literal null}.
     * @param value 值
     * @return 返回列表的长度
     * @see <a href="https://redis.io/commands/rpushx">Redis Documentation: RPUSHX</a>
     */
    @Nullable
    public Long rPushX(@NonNull String key, Object value) {
        return listOps.rightPushIfPresent(key, value);
    }

    /**
     * 移除并返回列表 key 的头元素
     *
     * @param key 一定不能为  {@literal null}.
     * @return 列表的头元素。 当 key 不存在时，返回 nil 。
     * @see <a href="https://redis.io/commands/lpop">Redis Documentation: LPOP</a>
     */
    @Nullable
    public <T> T lPop(@NonNull String key) {
        return (T) listOps.leftPop(key);
    }


    /**
     * 移除并返回列表 key 的尾元素。
     *
     * @param key 一定不能为  {@literal null}.
     * @return 列表的尾元素。 当 key 不存在时，返回 nil 。
     * @see <a href="https://redis.io/commands/rpop">Redis Documentation: RPOP</a>
     */
    public <T> T rPop(@NonNull String key) {
        return (T) listOps.rightPop(key);
    }


    /**
     * 命令 RPOPLPUSH 在一个原子时间内，执行以下两个动作：
     * 1.将列表 source 中的最后一个元素(尾元素)弹出，并返回给客户端。
     * 2. 将 source 弹出的元素插入到列表 destination ，作为 destination 列表的的头元素。
     * <p>
     * 举个例子，你有两个列表 source 和 destination ， source 列表有元素 a, b, c ， destination 列表有元素 x, y, z ，
     * 执行 RPOPLPUSH source destination 之后， source 列表包含元素 a, b ，
     * destination 列表包含元素 c, x, y, z ，并且元素 c 会被返回给客户端。
     * <p>
     * 如果 source 不存在，值 nil 被返回，并且不执行其他动作。
     * 如果 source 和 destination 相同，则列表中的表尾元素被移动到表头，并返回该元素，可以把这种特殊情况视作列表的旋转(rotation)操作。
     *
     * @param sourceKey      一定不能为 {@literal null}.
     * @param destinationKey 一定不能为 {@literal null}.
     * @return 被弹出的元素
     * @see <a href="https://redis.io/commands/rpoplpush">Redis Documentation: RPOPLPUSH</a>
     */
    public <T> T rPoplPush(String sourceKey, String destinationKey) {
        return (T) listOps.rightPopAndLeftPush(sourceKey, destinationKey);
    }

    /**
     * 根据参数 count 的值，移除列表中与参数 value 相等的元素。
     * <p>
     * count 的值可以是以下几种：
     * count > 0 : 从表头开始向表尾搜索，移除与 value 相等的元素，数量为 count 。
     * count {@code <} 0 : 从表尾开始向表头搜索，移除与 value 相等的元素，数量为 count 的绝对值。
     * count = 0 : 移除表中所有与 value 相等的值。
     *
     * @param key   一定不能为 {@literal null}.
     * @param count 数量
     * @param value 值
     * @return 被移除元素的数量。 因为不存在的 key 被视作空表(empty list)，所以当 key 不存在时， LREM 命令总是返回 0 。
     * @see <a href="https://redis.io/commands/lrem">Redis Documentation: LREM</a>
     */
    @Nullable
    public Long lRem(@NonNull String key, long count, Object value) {
        return listOps.remove(key, count, value);
    }

    /**
     * 返回列表 key 的长度。
     * 如果 key 不存在，则 key 被解释为一个空列表，返回 0 .
     * 如果 key 不是列表类型，返回一个错误。
     *
     * @param key 一定不能为 {@literal null}.
     * @return {列表 key 的长度
     * @see <a href="https://redis.io/commands/llen">Redis Documentation: LLEN</a>
     */
    @Nullable
    public Long lLen(@NonNull String key) {
        return listOps.size(key);
    }

    /**
     * 返回列表 key 中，下标为 index 的元素。
     * 下标(index)参数 start 和 stop 都以 0 为底，也就是说，以 0 表示列表的第一个元素，以 1 表示列表的第二个元素，以此类推。
     * 你也可以使用负数下标，以 -1 表示列表的最后一个元素， -2 表示列表的倒数第二个元素，以此类推。
     * 如果 key 不是列表类型，返回一个错误。
     *
     * @param key   一定不能为 {@literal null}.
     * @param index 索引
     * @return 列表中下标为 index 的元素。 如果 index 参数的值不在列表的区间范围内(out of range)，返回 nil 。
     * @see <a href="https://redis.io/commands/lindex">Redis Documentation: LINDEX</a>
     */
    @Nullable
    public <T> T lIndex(@NonNull String key, long index) {
        return (T) listOps.index(key, index);
    }


    /**
     * 将值 value 插入到列表 key 当中，位于值 pivot 之前。
     * 当 pivot 不存在于列表 key 时，不执行任何操作。
     * 当 key 不存在时， key 被视为空列表，不执行任何操作。
     * 如果 key 不是列表类型，返回一个错误。
     *
     * @param key   一定不能为 {@literal null}.
     * @param pivot 对比值
     * @param value 值
     * @return 如果命令执行成功，返回插入操作完成之后，列表的长度。 如果没有找到 pivot ，返回 -1 。 如果 key 不存在或为空列表，返回 0 。
     * @see <a href="https://redis.io/commands/linsert">Redis Documentation: LINSERT</a>
     */
    @Nullable
    public Long lInsert(@NonNull String key, Object pivot, Object value) {
        return listOps.leftPush(key, pivot, value);
    }

    /**
     * 将值 value 插入到列表 key 当中，位于值 pivot 之后。
     * 当 pivot 不存在于列表 key 时，不执行任何操作。
     * 当 key 不存在时， key 被视为空列表，不执行任何操作。
     * 如果 key 不是列表类型，返回一个错误。
     *
     * @param key   一定不能为 {@literal null}.
     * @param pivot 对比值
     * @param value 值
     * @return 如果命令执行成功，返回插入操作完成之后，列表的长度。 如果没有找到 pivot ，返回 -1 。 如果 key 不存在或为空列表，返回 0 。
     * @see <a href="https://redis.io/commands/linsert">Redis Documentation: LINSERT</a>
     */
    @Nullable
    public Long rInsert(@NonNull String key, Object pivot, Object value) {
        return listOps.rightPush(key, pivot, value);
    }

    /**
     * 将列表 key 下标为 index 的元素的值设置为 value 。
     * 当 index 参数超出范围，或对一个空列表( key 不存在)进行 LSET 时，返回一个错误。
     * 关于列表下标的更多信息，请参考 LINDEX 命令。
     *
     * @param key   一定不能为 {@literal null}.
     * @param index 下标
     * @param value 值
     * @see <a href="https://redis.io/commands/lset">Redis Documentation: LSET</a>
     */
    public void lSet(@NonNull String key, long index, Object value) {
        listOps.set(key, index, value);
    }

    /**
     * 返回列表 key 中指定区间内的元素，区间以偏移量 start 和 stop 指定。
     * 下标(index)参数 start 和 stop 都以 0 为底，也就是说，以 0 表示列表的第一个元素，以 1 表示列表的第二个元素，以此类推。
     * 你也可以使用负数下标，以 -1 表示列表的最后一个元素， -2 表示列表的倒数第二个元素，以此类推。
     *
     * <pre>
     * 例子：
     * 获取 list 中所有数据：lRange(key, 0, -1);
     * 获取 list 中下标 1 到 3 的数据： lRange(key, 1, 3);
     * </pre>
     * <p>
     * 如果 start 下标比列表的最大下标 end ( LLEN list 减去 1 )还要大，那么 LRANGE 返回一个空列表。
     * 如果 stop 下标比 end 下标还要大，Redis将 stop 的值设置为 end 。
     *
     * @param key   一定不能为 {@literal null}.
     * @param start 开始索引
     * @param end   结束索引
     * @return 一个列表，包含指定区间内的元素。
     * @see <a href="https://redis.io/commands/lrange">Redis Documentation: LRANGE</a>
     */
    @Nullable
    public List<Object> lRange(@NonNull String key, long start, long end) {
        return listOps.range(key, start, end);
    }

    /**
     * 对一个列表进行修剪(trim)，就是说，让列表只保留指定区间内的元素，不在指定区间之内的元素都将被删除。
     * 举个例子，执行命令 LTRIM list 0 2 ，表示只保留列表 list 的前三个元素，其余元素全部删除。
     * 下标(index)参数 start 和 stop 都以 0 为底，也就是说，以 0 表示列表的第一个元素，以 1 表示列表的第二个元素，以此类推。
     * 你也可以使用负数下标，以 -1 表示列表的最后一个元素， -2 表示列表的倒数第二个元素，以此类推。
     * 当 key 不是列表类型时，返回一个错误。
     *
     * @param key   一定不能为 {@literal null}.
     * @param start 开始索引
     * @param end   结束索引
     * @see <a href="https://redis.io/commands/ltrim">Redis Documentation: LTRIM</a>
     */
    public void lTrim(@NonNull String key, long start, long end) {
        listOps.trim(key, start, end);
    }

    // ---------------------------- list end ----------------------------

    // ---------------------------- set start ----------------------------

    /**
     * 将一个或多个 member 元素加入到集合 key 当中，已经存在于集合的 member 元素将被忽略。
     * 假如 key 不存在，则创建一个只包含 member 元素作成员的集合。
     * 当 key 不是集合类型时，返回一个错误。
     *
     * @param key     一定不能为 {@literal null}.
     * @param members 元素
     * @return 被添加到集合中的新元素的数量，不包括被忽略的元素。
     * @see <a href="https://redis.io/commands/sadd">Redis Documentation: SADD</a>
     */
    public <V> Long sAdd(@NonNull CacheKey key, V... members) {
        Long count = setOps.add(key.getKey(), members);
        setExpire(key);
        return count;
    }

    /**
     * 将一个或多个 member 元素加入到集合 key 当中，已经存在于集合的 member 元素将被忽略。
     * 假如 key 不存在，则创建一个只包含 member 元素作成员的集合。
     * 当 key 不是集合类型时，返回一个错误。
     *
     * @param key     一定不能为 {@literal null}.
     * @param members 元素
     * @return 被添加到集合中的新元素的数量，不包括被忽略的元素。
     * @see <a href="https://redis.io/commands/sadd">Redis Documentation: SADD</a>
     */
    public <V> Long sAdd(@NonNull CacheKey key, Collection<V> members) {
        Long count = setOps.add(key.getKey(), members.toArray());
        setExpire(key);
        return count;
    }

    /**
     * 判断 member 元素是否集合 key 的成员。
     *
     * @param key    一定不能为 {@literal null}.
     * @param member 元素
     * @return 如果 member 元素是集合的成员，返回 1 。 如果 member 元素不是集合的成员，或 key 不存在，返回 0 。
     * @see <a href="https://redis.io/commands/sismember">Redis Documentation: SISMEMBER</a>
     */
    public Boolean sIsMember(@NonNull CacheKey key, Object member) {
        return setOps.isMember(key.getKey(), member);
    }

    /**
     * 移除并返回集合中的一个随机元素。
     * 如果只想获取一个随机元素，但不想该元素从集合中被移除的话，可以使用 SRANDMEMBER 命令。
     *
     * @param key 一定不能为 {@literal null}.
     * @return 被移除的随机元素。 当 key 不存在或 key 是空集时，返回 nil 。
     * @see <a href="https://redis.io/commands/spop">Redis Documentation: SPOP</a>
     */
    @Nullable
    public <T> T sPop(@NonNull CacheKey key) {
        return (T) setOps.pop(key.getKey());
    }

    /**
     * 返回集合中的一个随机元素。
     *
     * @param key 一定不能为 {@literal null}.
     * @return 只提供 key 参数时，返回一个元素；如果集合为空，返回 nil
     * @see <a href="https://redis.io/commands/srandmember">Redis Documentation: SRANDMEMBER</a>
     */
    @Nullable
    public <T> T sRandMember(@NonNull CacheKey key) {
        return (T) setOps.randomMember(key.getKey());
    }

    /**
     * 返回集合中的count个随机元素。
     * <p>
     * 如果 count 为正数，且小于集合基数，那么命令返回一个包含 count 个元素的数组，数组中的元素各不相同。如果 count 大于等于集合基数，那么返回整个集合。
     * 如果 count 为负数，那么命令返回一个数组，数组中的元素可能会重复出现多次，而数组的长度为 count 的绝对值。
     *
     * @param key   一定不能为 {@literal null}.
     * @param count 数量
     * @return 只提供 key 参数时，返回一个元素；如果集合为空，返回 nil
     * @see <a href="https://redis.io/commands/srandmember">Redis Documentation: SRANDMEMBER</a>
     */
    @Nullable
    public <V> Set<V> sRandMember(@NonNull CacheKey key, long count) {
        return (Set<V>) setOps.distinctRandomMembers(key.getKey(), count);
    }


    /**
     * 返回集合中的count个随机元素。
     * <p>
     * 如果 count 为正数，且小于集合基数，那么命令返回一个包含 count 个元素的数组，数组中的元素各不相同。如果 count 大于等于集合基数，那么返回整个集合。
     * 如果 count 为负数，那么命令返回一个数组，数组中的元素可能会重复出现多次，而数组的长度为 count 的绝对值。
     *
     * @param key   一定不能为 {@literal null}.
     * @param count 数量
     * @return 只提供 key 参数时，返回一个元素；如果集合为空，返回 nil
     * @see <a href="https://redis.io/commands/srandmember">Redis Documentation: SRANDMEMBER</a>
     */
    @Nullable
    public <V> List<V> sRandMembers(@NonNull CacheKey key, long count) {
        return (List<V>) setOps.randomMembers(key.getKey(), count);
    }

    /**
     * 移除集合 key 中的一个或多个 member 元素，不存在的 member 元素会被忽略。
     * 当 key 不是集合类型，返回一个错误。
     *
     * @param key     一定不能为 {@literal null}.
     * @param members 元素
     * @return 被成功移除的元素的数量，不包括被忽略的元素
     * @see <a href="https://redis.io/commands/srem">Redis Documentation: SREM</a>
     */
    @Nullable
    public Long sRem(@NonNull CacheKey key, Object... members) {
        return setOps.remove(key.getKey(), members);
    }

    /**
     * 将 member 元素从 source 集合移动到 destination 集合。
     * SMOVE 是原子性操作。
     * 如果 source 集合不存在或不包含指定的 member 元素，则 SMOVE 命令不执行任何操作，仅返回 0 。否则， member 元素从 source 集合中被移除，并添加到 destination 集合中去。
     * 当 destination 集合已经包含 member 元素时， SMOVE 命令只是简单地将 source 集合中的 member 元素删除。
     * 当 source 或 destination 不是集合类型时，返回一个错误。
     *
     * @param sourceKey      源key
     * @param destinationKey 目的key
     * @param value          值
     * @return 是否成功
     * @see <a href="https://redis.io/commands/smove">Redis Documentation: SMOVE</a>
     */
    public <V> Boolean sMove(@NonNull CacheKey sourceKey, CacheKey destinationKey, V value) {
        return setOps.move(sourceKey.getKey(), value, destinationKey.getKey());
    }

    /**
     * 返回集合 key 的基数(集合中元素的数量)。
     *
     * @param key 一定不能为 {@literal null}.
     * @return 集合的基数。 当 key 不存在时，返回 0 。
     * @see <a href="https://redis.io/commands/scard">Redis Documentation: SCARD</a>
     */
    public Long sCard(@NonNull CacheKey key) {
        return setOps.size(key.getKey());
    }

    /**
     * 返回集合 key 中的所有成员。
     * 不存在的 key 被视为空集合。
     *
     * @param key 一定不能为 {@literal null}.
     * @return 集合中的所有成员。
     * @see <a href="https://redis.io/commands/smembers">Redis Documentation: SMEMBERS</a>
     */
    @Nullable
    public <V> Set<V> sMembers(@NonNull CacheKey key) {
        return (Set<V>) setOps.members(key.getKey());
    }


    /**
     * 返回一个集合的全部成员，该集合是所有给定集合的交集。
     * <p>
     * 不存在的 key 被视为空集。
     * <p>
     * 当给定集合当中有一个空集时，结果也为空集(根据集合运算定律)。
     *
     * @param key      一定不能为{@literal null}.
     * @param otherKey 一定不能为 {@literal null}.
     * @return 交集成员的列表。
     * @see <a href="https://redis.io/commands/sinter">Redis Documentation: SINTER</a>
     */
    @Nullable
    public <V> Set<V> sInter(@NonNull CacheKey key, @NonNull CacheKey otherKey) {
        return (Set<V>) setOps.intersect(key.getKey(), otherKey.getKey());
    }

    /**
     * 返回一个集合的全部成员，该集合是所有给定集合的交集。
     * <p>
     * 不存在的 key 被视为空集。
     * <p>
     * 当给定集合当中有一个空集时，结果也为空集(根据集合运算定律)。
     *
     * @param key       一定不能为{@literal null}.
     * @param otherKeys 一定不能为 {@literal null}.
     * @return 交集成员的列表。
     * @see <a href="https://redis.io/commands/sinter">Redis Documentation: SINTER</a>
     */
    @Nullable
    public Set<Object> sInter(@NonNull CacheKey key, Collection<CacheKey> otherKeys) {
        return setOps.intersect(key.getKey(), otherKeys.stream().map(CacheKey::getKey).toList());
    }

    /**
     * 返回一个集合的全部成员，该集合是所有给定集合的交集。
     * <p>
     * 不存在的 key 被视为空集。
     * <p>
     * 当给定集合当中有一个空集时，结果也为空集(根据集合运算定律)。
     *
     * @param otherKeys 一定不能为 {@literal null}.
     * @return 交集成员的列表。
     * @see <a href="https://redis.io/commands/sinter">Redis Documentation: SINTER</a>
     */
    @Nullable
    public <V> Set<V> sInter(Collection<CacheKey> otherKeys) {
        return (Set<V>) setOps.intersect(otherKeys.stream().map(CacheKey::getKey).toList());
    }


    /**
     * 这个命令类似于 SINTER key [key …] 命令，但它将结果保存到 destination 集合，而不是简单地返回结果集。
     * 如果 destination 集合已经存在，则将其覆盖。
     * destination 可以是 key 本身。
     *
     * @param key      一定不能为{@literal null}.
     * @param otherKey 一定不能为 {@literal null}.
     * @param destKey  一定不能为{@literal null}.
     * @return 结果集中的成员数量。
     * @see <a href="https://redis.io/commands/sinterstore">Redis Documentation: SINTERSTORE</a>
     */
    @Nullable
    public Long sInterStore(@NonNull CacheKey key, @NonNull CacheKey otherKey, @NonNull CacheKey destKey) {
        return setOps.intersectAndStore(key.getKey(), otherKey.getKey(), destKey.getKey());
    }

    /**
     * 这个命令类似于 SINTER key [key …] 命令，但它将结果保存到 destination 集合，而不是简单地返回结果集。
     * 如果 destination 集合已经存在，则将其覆盖。
     * destination 可以是 key 本身。
     *
     * @param key       一定不能为{@literal null}.
     * @param otherKeys 一定不能为 {@literal null}.
     * @param destKey   一定不能为{@literal null}.
     * @return 结果集中的成员数量。
     * @see <a href="https://redis.io/commands/sinterstore">Redis Documentation: SINTERSTORE</a>
     */
    @Nullable
    public Long sInterStore(@NonNull CacheKey key, @NonNull Collection<CacheKey> otherKeys, @NonNull CacheKey destKey) {
        return setOps.intersectAndStore(key.getKey(),
                otherKeys.stream().map(CacheKey::getKey).toList(), destKey.getKey());
    }

    /**
     * 这个命令类似于 SINTER key [key …] 命令，但它将结果保存到 destination 集合，而不是简单地返回结果集。
     * 如果 destination 集合已经存在，则将其覆盖。
     * destination 可以是 key 本身。
     *
     * @param otherKeys 一定不能为 {@literal null}.
     * @param destKey   一定不能为{@literal null}.
     * @return 结果集中的成员数量。
     * @see <a href="https://redis.io/commands/sinterstore">Redis Documentation: SINTERSTORE</a>
     */
    @Nullable
    public Long sInterStore(Collection<CacheKey> otherKeys, @NonNull CacheKey destKey) {
        return setOps.intersectAndStore(otherKeys.stream().map(CacheKey::getKey).toList(), destKey.getKey());
    }


    /**
     * 返回多个集合的并集，多个集合由 keys 指定
     * 不存在的 key 被视为空集。
     *
     * @param key      一定不能为 {@literal null}.
     * @param otherKey 一定不能为 {@literal null}.
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/sunion">Redis Documentation: SUNION</a>
     */
    @Nullable
    public <V> Set<V> sUnion(@NonNull CacheKey key, @NonNull CacheKey otherKey) {
        return (Set<V>) setOps.union(key.getKey(), otherKey.getKey());
    }

    /**
     * 返回多个集合的并集，多个集合由 keys 指定
     * 不存在的 key 被视为空集。
     *
     * @param key       一定不能为 {@literal null}.
     * @param otherKeys 一定不能为 {@literal null}.
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/sunion">Redis Documentation: SUNION</a>
     */
    @Nullable
    public <V> Set<V> sUnion(@NonNull CacheKey key, Collection<CacheKey> otherKeys) {
        return (Set<V>) setOps.union(key.getKey(), otherKeys.stream().map(CacheKey::getKey).toList());
    }

    /**
     * 返回多个集合的并集，多个集合由 keys 指定
     * 不存在的 key 被视为空集。
     *
     * @param otherKeys 一定不能为 {@literal null}.
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/sunion">Redis Documentation: SUNION</a>
     */
    @Nullable
    public <V> Set<V> sUnion(Collection<CacheKey> otherKeys) {
        return (Set<V>) setOps.union(otherKeys.stream().map(CacheKey::getKey).toList());
    }

    /**
     * 这个命令类似于 SUNION key [key …] 命令，但它将结果保存到 destination 集合，而不是简单地返回结果集。
     * 如果 destination 已经存在，则将其覆盖。
     * destination 可以是 key 本身。
     *
     * @param key      一定不能为 {@literal null}.
     * @param otherKey 一定不能为 {@literal null}.
     * @param distKey  一定不能为 {@literal null}.
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/sunionstore">Redis Documentation: SUNIONSTORE</a>
     */
    public Long sUnionStore(@NonNull CacheKey key, @NonNull CacheKey otherKey, @NonNull CacheKey distKey) {
        return setOps.unionAndStore(key.getKey(), otherKey.getKey(), distKey.getKey());
    }

    /**
     * 这个命令类似于 SUNION key [key …] 命令，但它将结果保存到 destination 集合，而不是简单地返回结果集。
     * 如果 destination 已经存在，则将其覆盖。
     * destination 可以是 key 本身。
     *
     * @param otherKeys 一定不能为 {@literal null}.
     * @param distKey   一定不能为 {@literal null}.
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/sunionstore">Redis Documentation: SUNIONSTORE</a>
     */
    public Long sUnionStore(Collection<CacheKey> otherKeys, @NonNull CacheKey distKey) {
        return setOps.unionAndStore(otherKeys.stream().map(CacheKey::getKey).toList(), distKey.getKey());
    }

    /**
     * 返回一个集合的全部成员，该集合是所有给定集合之间的差集。
     * 不存在的 key 被视为空集。
     *
     * @param key      一定不能为 {@literal null}.
     * @param otherKey 一定不能为 {@literal null}.
     * @return 一个包含差集成员的列表。
     * @see <a href="https://redis.io/commands/sdiff">Redis Documentation: SDIFF</a>
     */
    @Nullable
    public <V> Set<V> sDiff(@NonNull CacheKey key, @NonNull CacheKey otherKey) {
        return (Set<V>) setOps.difference(key.getKey(), otherKey.getKey());
    }

    /**
     * 返回一个集合的全部成员，该集合是所有给定集合之间的差集。
     * 不存在的 key 被视为空集。
     *
     * @param otherKeys 一定不能为 {@literal null}.
     * @return 一个包含差集成员的列表。
     * @see <a href="https://redis.io/commands/sdiff">Redis Documentation: SDIFF</a>
     */
    public <V> Set<V> sDiff(Collection<CacheKey> otherKeys) {
        return (Set<V>) setOps.difference(otherKeys.stream().map(CacheKey::getKey).toList());
    }

    /**
     * 这个命令的作用和 SDIFF key [key …] 类似，但它将结果保存到 destination 集合，而不是简单地返回结果集。
     * 如果 destination 集合已经存在，则将其覆盖。
     * destination 可以是 key 本身。
     *
     * @param key      一定不能为 {@literal null}.
     * @param distKey  一定不能为 {@literal null}.
     * @param otherKey 一定不能为 {@literal null}.
     * @return 结果集中的元素数量。。
     * @see <a href="https://redis.io/commands/sdiffstore">Redis Documentation: sdiffstore</a>
     */
    public Long sDiffStore(@NonNull CacheKey key, @NonNull CacheKey otherKey, @NonNull CacheKey distKey) {
        return setOps.differenceAndStore(key.getKey(), otherKey.getKey(), distKey.getKey());
    }

    /**
     * 返回一个集合的全部成员，该集合是所有给定集合之间的差集。
     * 不存在的 key 被视为空集。
     *
     * @param otherKeys 一定不能为 {@literal null}.
     * @return 结果集中的元素数量。
     * @see <a href="https://redis.io/commands/sdiffstore">Redis Documentation: sdiffstore</a>
     */
    public Long sDiffStore(Collection<CacheKey> otherKeys, @NonNull CacheKey distKey) {
        return setOps.differenceAndStore(otherKeys.stream().map(CacheKey::getKey).toList(),
                distKey.getKey());
    }


    // ---------------------------- set end ----------------------------

    // ---------------------------- zSet start ----------------------------

    /**
     * 将一个或多个 member 元素及其 score 值加入到有序集 key 当中。
     * 如果某个 member 已经是有序集的成员，那么更新这个 member 的 score 值，并通过重新插入这个 member 元素，来保证该 member 在正确的位置上。
     * score 值可以是整数值或双精度浮点数。
     * 如果 key 不存在，则创建一个空的有序集并执行 ZADD 操作。
     * 当 key 存在但不是有序集类型时，返回一个错误。
     *
     * @param key    一定不能为 {@literal null}.
     * @param score  得分
     * @param member 值
     * @return 是否成功
     * @see <a href="https://redis.io/commands/zadd">Redis Documentation: ZADD</a>
     */
    public Boolean zAdd(@NonNull String key, Object member, double score) {
        return zSetOps.add(key, member, score);
    }

    /**
     * 将一个或多个 member 元素及其 score 值加入到有序集 key 当中。
     * 如果某个 member 已经是有序集的成员，那么更新这个 member 的 score 值，并通过重新插入这个 member 元素，来保证该 member 在正确的位置上。
     * score 值可以是整数值或双精度浮点数。
     * 如果 key 不存在，则创建一个空的有序集并执行 ZADD 操作。
     * 当 key 存在但不是有序集类型时，返回一个错误。
     *
     * @param key          一定不能为 {@literal null}.
     * @param scoreMembers 一定不能为 {@literal null}.
     * @return 被成功添加的新成员的数量，不包括那些被更新的、已经存在的成员。
     * @see <a href="https://redis.io/commands/zadd">Redis Documentation: ZADD</a>
     */
    public Long zAdd(@NonNull String key, Map<Object, Double> scoreMembers) {
        Set<ZSetOperations.TypedTuple<Object>> tuples = new HashSet<>();
        scoreMembers.forEach((score, member) -> tuples.add(new DefaultTypedTuple<>(score, member)));
        return zSetOps.add(key, tuples);
    }

    /**
     * 返回有序集 key 中，成员 member 的 score 值。
     * 如果 member 元素不是有序集 key 的成员，或 key 不存在，返回 nil 。
     *
     * @param key    一定不能为 {@literal null}.
     * @param member the value.
     * @return member 成员的 score 值，以字符串形式表示
     * @see <a href="https://redis.io/commands/zscore">Redis Documentation: ZSCORE</a>
     */
    public Double zScore(@NonNull String key, Object member) {
        return zSetOps.score(key, member);
    }

    /**
     * 为有序集 key 的成员 member 的 score 值加上增量 increment 。
     * 可以通过传递一个负数值 increment ，让 score 减去相应的值，比如 ZINCRBY key -5 member ，就是让 member 的 score 值减去 5 。
     * 当 key 不存在，或 member 不是 key 的成员时， ZINCRBY key increment member 等同于 ZADD key increment member 。
     * 当 key 不是有序集类型时，返回一个错误。
     * score 值可以是整数值或双精度浮点数。
     *
     * @param key    一定不能为 {@literal null}.
     * @param score  得分
     * @param member the value.
     * @return member 成员的新 score 值
     * @see <a href="https://redis.io/commands/zincrby">Redis Documentation: ZINCRBY</a>
     */
    public Double zIncrBy(@NonNull String key, Object member, double score) {
        return zSetOps.incrementScore(key, member, score);
    }

    /**
     * 返回有序集 key 的基数。
     *
     * @param key 一定不能为 {@literal null}.
     * @return 当 key 存在且是有序集类型时，返回有序集的基数。 当 key 不存在时，返回 0 。
     * @see <a href="https://redis.io/commands/zcard">Redis Documentation: ZCARD</a>
     */
    public Long zCard(@NonNull String key) {
        return zSetOps.zCard(key);
    }

    /**
     * 返回有序集 key 中， score 值在 min 和 max 之间(默认包括 score 值等于 min 或 max )的成员的数量。
     *
     * @param key 一定不能为 {@literal null}.
     * @param min 最小值
     * @param max 最大值
     * @return {@literal null} when used in pipeline / transaction.
     * @see <a href="https://redis.io/commands/zcount">Redis Documentation: ZCOUNT</a>
     */
    public Long zCount(@NonNull String key, double min, double max) {
        return zSetOps.count(key, min, max);
    }

    /**
     * 返回有序集 key 中，指定区间内的成员。
     * 其中成员的位置按 score 值递增(从小到大)来排序。
     * 具有相同 score 值的成员按字典序(lexicographical order )来排列。
     * <p>
     * 下标参数 start 和 stop 都以 0 为底，也就是说，以 0 表示有序集第一个成员，以 1 表示有序集第二个成员，以此类推。 你也可以使用负数下标，以 -1 表示最后一个成员， -2 表示倒数第二个成员，以此类推。
     * <p>
     * 超出范围的下标并不会引起错误。 比如说，当 start 的值比有序集的最大下标还要大，或是 start > stop 时， ZRANGE 命令只是简单地返回一个空列表。 另一方面，假如 stop 参数的值比有序集的最大下标还要大，那么 Redis 将 stop 当作最大下标来处理。
     * <p>
     * 可以通过使用 WITHSCORES 选项，来让成员和它的 score 值一并返回，返回列表以 value1,score1, ..., valueN,scoreN 的格式表示。 客户端库可能会返回一些更复杂的数据类型，比如数组、元组等
     *
     * @param key   一定不能为 {@literal null}.
     * @param start 索引
     * @param end   索引
     * @return 指定区间内，不带有 score 值(可选)的有序集成员的列表。
     * @see <a href="https://redis.io/commands/zrange">Redis Documentation: ZRANGE</a>
     */
    @Nullable
    public Set<Object> zRange(@NonNull String key, long start, long end) {
        return zSetOps.range(key, start, end);
    }

    /**
     * 返回有序集 key 中，指定区间内的成员。
     * 其中成员的位置按 score 值递增(从小到大)来排序。
     * 具有相同 score 值的成员按字典序(lexicographical order )来排列。
     * <p>
     * 下标参数 start 和 stop 都以 0 为底，也就是说，以 0 表示有序集第一个成员，以 1 表示有序集第二个成员，以此类推。 你也可以使用负数下标，以 -1 表示最后一个成员， -2 表示倒数第二个成员，以此类推。
     * <p>
     * 超出范围的下标并不会引起错误。 比如说，当 start 的值比有序集的最大下标还要大，或是 start > stop 时， ZRANGE 命令只是简单地返回一个空列表。 另一方面，假如 stop 参数的值比有序集的最大下标还要大，那么 Redis 将 stop 当作最大下标来处理。
     * <p>
     * 可以通过使用 WITHSCORES 选项，来让成员和它的 score 值一并返回，返回列表以 value1,score1, ..., valueN,scoreN 的格式表示。 客户端库可能会返回一些更复杂的数据类型，比如数组、元组等
     *
     * @param key   一定不能为 {@literal null}.
     * @param start 索引
     * @param end   索引
     * @return 指定区间内，带有 score 值(可选)的有序集成员的列表。
     * @see <a href="https://redis.io/commands/zrange">Redis Documentation: ZRANGE</a>
     */
    @Nullable
    public Set<ZSetOperations.TypedTuple<Object>> zRangeWithScores(@NonNull String key, long start, long end) {
        return zSetOps.rangeWithScores(key, start, end);
    }

    /**
     * 返回有序集 key 中，指定区间内的成员。
     * 其中成员的位置按 score 值递减(从大到小)来排列。 具有相同 score 值的成员按字典序的逆序(reverse lexicographical order)排列。
     * 除了成员按 score 值递减的次序排列这一点外， ZREVRANGE 命令的其他方面和 ZRANGE key start stop [WITHSCORES] 命令一样。
     *
     * @param key   一定不能为 {@literal null}.
     * @param start 索引
     * @param end   索引
     * @return 指定区间内，不带有 score 值(可选)的有序集成员的列表。
     * @see <a href="https://redis.io/commands/zrevrange">Redis Documentation: ZREVRANGE</a>
     */
    @Nullable
    public Set<Object> zRevrange(@NonNull String key, long start, long end) {
        return zSetOps.reverseRange(key, start, end);
    }

    /**
     * 返回有序集 key 中，指定区间内的成员。
     * 其中成员的位置按 score 值递减(从大到小)来排列。 具有相同 score 值的成员按字典序的逆序(reverse lexicographical order)排列。
     * 除了成员按 score 值递减的次序排列这一点外， ZREVRANGE 命令的其他方面和 ZRANGE key start stop [WITHSCORES] 命令一样。
     *
     * @param key   一定不能为 {@literal null}.
     * @param start 索引
     * @param end   索引
     * @return 指定区间内，不带有 score 值(可选)的有序集成员的列表。
     * @see <a href="https://redis.io/commands/zrevrange">Redis Documentation: ZREVRANGE</a>
     */
    @Nullable
    public Set<ZSetOperations.TypedTuple<Object>> zRevrangeWithScores(@NonNull String key, long start, long end) {
        return zSetOps.reverseRangeWithScores(key, start, end);
    }

    /**
     * 返回有序集 key 中，所有 score 值介于 min 和 max 之间(包括等于 min 或 max )的成员。
     * 有序集成员按 score 值递增(从小到大)次序排列。
     * 具有相同 score 值的成员按字典序(lexicographical order)来排列(该属性是有序集提供的，不需要额外的计算)。
     *
     * @param key 一定不能为 {@literal null}.
     * @param min 最小得分
     * @param max 最大得分
     * @return 指定区间内 不带有 score 值(可选)的有序集成员的列表。
     * @see <a href="https://redis.io/commands/zrangebyscore">Redis Documentation: ZRANGEBYSCORE</a>
     */
    public Set<Object> zRangeByScore(@NonNull String key, double min, double max) {
        return zSetOps.rangeByScore(key, min, max);
    }

    /**
     * 返回有序集 key 中，所有 score 值介于 min 和 max 之间(包括等于 min 或 max )的成员。
     * 有序集成员按 score 值递增(从小到大)次序排列。
     * 具有相同 score 值的成员按字典序(lexicographical order)来排列(该属性是有序集提供的，不需要额外的计算)。
     *
     * @param key 一定不能为 {@literal null}.
     * @param min 最小得分
     * @param max 最大得分
     * @return 指定区间内，带有 score 值(可选)的有序集成员的列表。
     * @see <a href="https://redis.io/commands/zrangebyscore">Redis Documentation: ZRANGEBYSCORE</a>
     */
    public Set<ZSetOperations.TypedTuple<Object>> zRangeByScoreWithScores(@NonNull String key, double min, double max) {
        return zSetOps.rangeByScoreWithScores(key, min, max);
    }

	public Set<ZSetOperations.TypedTuple<Object>> zRangeByScoreWithScores(@NonNull String key, double min, double max, long offset, long count) {
		return zSetOps.rangeByScoreWithScores(key, min, max, offset, count);
	}

    /**
     * 返回有序集 key 中， score 值介于 max 和 min 之间(默认包括等于 max 或 min )的所有的成员。有序集成员按 score 值递减(从大到小)的次序排列。
     * 具有相同 score 值的成员按字典序的逆序(reverse lexicographical order )排列。
     *
     * @param key 一定不能为 {@literal null}.
     * @param min 最小得分
     * @param max 最大得分
     * @return 指定区间内 不带有 score 值(可选)的有序集成员的列表。
     * @see <a href="https://redis.io/commands/zrevrange">Redis Documentation: ZRANGEBYSCORE</a>
     */
    public Set<Object> zReverseRange(@NonNull String key, double min, double max) {
        return zSetOps.reverseRangeByScore(key, min, max);
    }

    /**
     * 返回有序集 key 中， score 值介于 max 和 min 之间(默认包括等于 max 或 min )的所有的成员。有序集成员按 score 值递减(从大到小)的次序排列。
     * 具有相同 score 值的成员按字典序的逆序(reverse lexicographical order )排列。
     *
     * @param key 一定不能为 {@literal null}.
     * @param min 最小得分
     * @param max 最大得分
     * @return 指定区间内，带有 score 值(可选)的有序集成员的列表。
     * @see <a href="https://redis.io/commands/zrevrangebyscore">Redis Documentation: ZRANGEBYSCORE</a>
     */
    public Set<ZSetOperations.TypedTuple<Object>> zReverseRangeByScoreWithScores(@NonNull String key, double min, double max) {
        return zSetOps.reverseRangeByScoreWithScores(key, min, max);
    }

	public Set<ZSetOperations.TypedTuple<Object>> zReverseRangeByScoreWithScores(String key, double min, double max, int offset, long pageSize) {
		return zSetOps.reverseRangeByScoreWithScores(key, min, max, offset, pageSize);
	}

	public Long lexCount(String key, Range<String> range) {
		return zSetOps.lexCount(key, range);
	}

    /**
     * 返回有序集 key 中成员 member 的排名。其中有序集成员按 score 值递增(从小到大)顺序排列。
     * 排名以 0 为底，也就是说， score 值最小的成员排名为 0 。
     * 使用 ZREVRANK key member 命令可以获得成员按 score 值递减(从大到小)排列的排名。
     *
     * @param key    一定不能为 {@literal null}.
     * @param member the value.
     * @return 如果 member 是有序集 key 的成员，返回 member 的排名。 如果 member 不是有序集 key 的成员，返回 nil 。
     * @see <a href="https://redis.io/commands/zrank">Redis Documentation: ZRANK</a>
     */
    @Nullable
    public Long zRank(@NonNull String key, Object member) {
        return zSetOps.rank(key, member);
    }

    /**
     * 返回有序集 key 中成员 member 的排名。其中有序集成员按 score 值递减(从大到小)排序。
     * 排名以 0 为底，也就是说， score 值最大的成员排名为 0 。
     * 使用 ZRANK 命令可以获得成员按 score 值递增(从小到大)排列的排名。
     *
     * @param key    一定不能为 {@literal null}.
     * @param member the value.
     * @return 如果 member 是有序集 key 的成员，返回 member 的排名。 如果 member 不是有序集 key 的成员，返回 nil 。
     * @see <a href="https://redis.io/commands/zrevrank">Redis Documentation: ZREVRANK</a>
     */
    public Long zRevrank(@NonNull String key, Object member) {
        return zSetOps.reverseRank(key, member);
    }

    /**
     * 移除有序集 key 中的一个或多个成员，不存在的成员将被忽略。
     * 当 key 存在但不是有序集类型时，返回一个错误。
     *
     * @param key     一定不能为 {@literal null}.
     * @param members 一定不能为 {@literal null}.
     * @return 被成功移除的成员的数量，不包括被忽略的成员
     * @see <a href="https://redis.io/commands/zrem">Redis Documentation: ZREM</a>
     */
    public Long zRem(@NonNull String key, Object... members) {
        return zSetOps.remove(key, members);
    }

    /**
     * 移除有序集 key 中，指定排名(rank)区间内的所有成员。
     * 区间分别以下标参数 start 和 stop 指出，包含 start 和 stop 在内。
     * 下标参数 start 和 stop 都以 0 为底，也就是说，以 0 表示有序集第一个成员，以 1 表示有序集第二个成员，以此类推。 你也可以使用负数下标，以 -1 表示最后一个成员， -2 表示倒数第二个成员，以此类推。
     *
     * @param key   一定不能为 {@literal null}.
     * @param start 下标
     * @param end   下标
     * @return 被移除成员的数量。
     * @see <a href="https://redis.io/commands/zremrangebyrank">Redis Documentation: ZREMRANGEBYRANK</a>
     */
    public Long zRem(@NonNull String key, long start, long end) {
        return zSetOps.removeRange(key, start, end);
    }

    /**
     * 移除有序集 key 中，所有 score 值介于 min 和 max 之间(包括等于 min 或 max )的成员。
     * 自版本2.1.6开始， score 值等于 min 或 max 的成员也可以不包括在内
     *
     * @param key 一定不能为 {@literal null}.
     * @param min 最小得分
     * @param max 最大得分
     * @return 被移除成员的数量。
     * @see <a href="https://redis.io/commands/zremrangebyscore">Redis Documentation: ZREMRANGEBYSCORE</a>
     */
    public Long zRemRangeByScore(@NonNull String key, double min, double max) {
        return zSetOps.removeRangeByScore(key, min, max);
    }

	public Long sSize(String key) {
		return setOps.size(key);
	}

	public void ZSetAddAndExpire(String key, long startTime, long expireTime, long currentTime) {
		zSetOps.add(key, String.valueOf(currentTime), currentTime);
		// 删除周期之前的数据
		zSetOps.removeRangeByScore(key, 0, startTime);
		// 过期时间窗口长度+时间间隔
		stringRedisTemplate.expire(key, expireTime, TimeUnit.MILLISECONDS);
	}

	/**
	 * 批量查询一个集合里面key的状态 [序列化必须要和zSetOps 的序列化一致!!! 不然查出来的数据永远是空，浪费我一小时排查]
	 * @return
	 */
	public List<Object> getZSetScores(String key, List<Long> fields) {
		return redisTemplate.executePipelined((RedisCallback<Object>) conn -> {
			RedisSerializer<String> keySerializer = (RedisSerializer<String>) redisTemplate.getKeySerializer();

			byte[] keyBytes = keySerializer.serialize(key);

			fields.forEach(id -> {
				RedisSerializer<Object> valueSerializer = (RedisSerializer<Object>) redisTemplate.getValueSerializer();

				byte[] memberBytes = valueSerializer.serialize(id.toString());
				conn.zSetCommands().zScore(keyBytes, memberBytes);
			});
			return null;
		});
	}

	public List<Long> sMultiCard(List<String> keys) {
		return redisTemplate.executePipelined((RedisCallback<Long>) connection -> {
					for (String key : keys) {
						connection.sCard(key.getBytes());
					}
					return null;
				}).stream()
				.map(obj -> (Long) obj)
				.collect(Collectors.toList());
	}
}
