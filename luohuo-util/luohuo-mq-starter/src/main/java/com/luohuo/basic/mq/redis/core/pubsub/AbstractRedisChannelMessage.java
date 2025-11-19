package com.luohuo.basic.mq.redis.core.pubsub;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.luohuo.basic.mq.redis.core.message.AbstractRedisMessage;

/**
 * Redis Channel Message 抽象类
 *
 * @author 乾乾
 */
public abstract class AbstractRedisChannelMessage extends AbstractRedisMessage {

    /**
     * 获得 Redis Channel，默认使用类名
     *
     * @return Channel
     */
    @JsonIgnore // 避免序列化。原因是，Redis 发布 Channel 消息的时候，已经会指定。
    public String getChannel() {
        return getClass().getSimpleName();
    }

}
