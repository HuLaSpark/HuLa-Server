package com.hula.common.interceptor;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.hula.snowflake.uid.UidGenerator;

/**
 * 自定义mybatisPlus雪花ID
 */
public class CustomSnowflakeIdGenerator implements IdentifierGenerator {

    private final UidGenerator uidGenerator;

    public CustomSnowflakeIdGenerator(UidGenerator uidGenerator) {
        this.uidGenerator = uidGenerator;
    }

    @Override
    public Number nextId(Object entity) {
        return uidGenerator.getUid();
    }
}