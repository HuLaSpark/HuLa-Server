package com.luohuo.basic.database.properties;

/**
 * @author 乾乾
 * @date 2020/9/15 3:44 下午
 */
public enum IdType {
    /**
     * 单机部署 或者 有固定部署集群数量时，使用hu tool的
     */
    HU_TOOL,
    /**
     * 集群部署动态扩容时使用
     * <p>
     * UidGenerator通过借用未来时间来解决sequence天然存在的并发限制
     */
    DEFAULT,
    /**
     * 集群部署动态扩容时使用
     * 采用RingBuffer来缓存已生成的UID, 并行化UID的生产和消费, 同时对CacheLine补齐，避免了由RingBuffer带来的硬件级「伪共享」问题
     * <p>
     * 600万/s的稳定吞吐量
     */
    CACHE,
    ;

    public boolean eq(IdType t) {
        return t != null && this.name().equals(t.name());
    }
}
