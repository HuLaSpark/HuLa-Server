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
	/** 溢出总页数后是否进行处理 */
	protected Boolean overflow = true;
	/** 是否启用 防止全表更新与删除插件 */
	private Boolean isBlockAttack = false;
	/** 分页最大数量 */
	private Long maxLimit = 500L;
}