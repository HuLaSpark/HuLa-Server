package com.luohuo.flex.common.cache;


import com.luohuo.basic.model.cache.CacheKeyBuilder;

import java.time.Duration;

/**
 * 短信验证码 KEY
 *
 * @author 乾乾
 * @date 2020/9/23 9:10 上午
 */
public class VerificationCodeCacheKeyBuilder implements CacheKeyBuilder {
    @Override
    public String getTable() {
        return CacheKeyTable.REGISTER_USER;
    }

    @Override
    public Duration getExpire() {
        return Duration.ofSeconds(5 * 60);
    }
}
