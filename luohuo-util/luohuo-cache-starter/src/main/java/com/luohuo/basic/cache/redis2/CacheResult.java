package com.luohuo.basic.cache.redis2;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import com.luohuo.basic.cache.redis.NullVal;
import com.luohuo.basic.exception.BizException;
import com.luohuo.basic.model.cache.CacheHashKey;
import com.luohuo.basic.model.cache.CacheKey;
import com.luohuo.basic.utils.ArgumentAssert;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 缓存返回对象
 *
 * @author tangyh
 * @version v1.0
 * @date 2022/10/24 9:25 AM
 * @create [2022/10/24 9:25 AM ] [tangyh] [初始创建]
 */
@Setter
@Getter
public class CacheResult<T> {
    private String key;
    private Object field;
    private Duration expire;
    private T rawValue;

    public CacheResult(String key) {
        this.key = key;
    }

    public CacheResult(String key, T rawValue) {
        this.key = key;
        this.rawValue = rawValue;
    }

    public CacheResult(String key, Duration expire, T rawValue) {
        this.key = key;
        this.expire = expire;
        this.rawValue = rawValue;
    }


    public CacheResult(CacheKey cacheKey) {
        ArgumentAssert.notNull(cacheKey, "key 不能为空");
        this.key = cacheKey.getKey();
        this.expire = cacheKey.getExpire();
    }

    public CacheResult(CacheKey cacheKey, T rawValue) {
        ArgumentAssert.notNull(cacheKey, "key 不能为空");
        this.key = cacheKey.getKey();
        this.expire = cacheKey.getExpire();
        this.rawValue = rawValue;
    }

    public CacheResult(CacheHashKey cacheKey, T rawValue) {
        ArgumentAssert.notNull(cacheKey, "key 不能为空");
        this.key = cacheKey.getKey();
        this.field = cacheKey.getField();
        this.expire = cacheKey.getExpire();
        this.rawValue = rawValue;
    }

    /**
     * 缓存对象
     *
     * @return cache object include null object
     */
    public T getValue() {
        boolean isNil = rawValue == null || NullVal.class.equals(rawValue.getClass());
        boolean isObj = rawValue == null || Object.class.equals(rawValue.getClass());
        boolean isEmpty = (rawValue instanceof Map map && map.isEmpty());
        if (isNil || isObj || isEmpty) {
            return null;
        }
        return rawValue;
    }


    /**
     * 是否缓存的空值
     *
     * @return 返回true 表示redis缓存了空值， 一般情况下数据库没有值才会缓存空值
     */
    @JsonIgnore
    public boolean isNullVal() {
        return rawValue != null &&
                (NullVal.class.equals(rawValue.getClass()) ||
                        (rawValue instanceof Map map && map.isEmpty()));
    }

    /**
     * 是否没有缓存
     *
     * @return 返回true 表示redis没有该值， 并不能确定数据库有无该值！
     */
    @JsonIgnore
    public boolean isNull() {
        return rawValue == null;
    }

    @JsonIgnore
    public <E> List<E> asList() {
        boolean isNil = rawValue == null || NullVal.class.equals(rawValue.getClass());
        boolean isObj = rawValue == null || Object.class.equals(rawValue.getClass());
        boolean isEmpty = (rawValue instanceof Map map && map.isEmpty());
        if (isNil || isObj || isEmpty) {
            return Collections.emptyList();
        }
        return (List<E>) rawValue;
    }

    @JsonIgnore
    public String asString() {
        return isNullVal() ? null : String.valueOf(rawValue);
    }

    @JsonIgnore
    public Long asLong() {
        boolean isNil = rawValue == null || NullVal.class.equals(rawValue.getClass());
        boolean isObj = rawValue == null || Object.class.equals(rawValue.getClass());
        boolean isEmpty = (rawValue instanceof Map map && map.isEmpty());
        if (isNil || isObj || isEmpty) {
            return null;
        }
        if (rawValue instanceof Long lo) {
            return lo;
        } else if (rawValue instanceof String str) {
            return Long.parseLong(str);
        } else {
            throw BizException.wrap("[{}]无法转换为Long类型", rawValue);
        }

    }

    @JsonIgnore
    public long asLong(long defValue) {
        try {
            return Long.parseLong(asString());
        } catch (Exception e) {
            return defValue;
        }
    }

    @Override
    public String toString() {
        return StrUtil.format("key={}, field={}, isNullVal={}, rawValue={}", key, field, isNullVal(), getValue());
    }
}
