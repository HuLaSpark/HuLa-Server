package com.hula.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "uid.database")
public class DatabaseProperties {
    /** 时间位分配 */
    private int timeBits = 41;
    /** 工作节点位分配 */
    private int workerBits = 13;
    /** 序列号位分配 */
    private int seqBits = 9;
    /** 起始时间（格式 yyyy-MM-dd） */
    private String epochStr = "2025-02-25";
    /** 随机序列上限 */
    private long randomSequenceLimit = 0L;
}