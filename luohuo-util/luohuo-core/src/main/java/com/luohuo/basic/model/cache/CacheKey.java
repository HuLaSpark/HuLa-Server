package com.luohuo.basic.model.cache;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

import java.time.Duration;

/**
 * 缓存 key 封装
 *
 * @author 乾乾
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CacheKey {
    /**
     * redis key
     */
    @NonNull
    private String key;
    /**
     * 超时时间 秒
     */
    private Duration expire;

    public CacheKey(final @NonNull String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "key=" + key + " , expire=" + expire;
    }
}
