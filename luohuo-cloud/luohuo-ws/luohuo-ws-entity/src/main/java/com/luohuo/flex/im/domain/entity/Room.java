package com.luohuo.flex.im.domain.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 房间表
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Room implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 房间类型 1群聊 2单聊
     */
    private Integer type;

    /**
     * 是否全员展示 0否 1是
     */
    private Integer hotFlag;

    /**
     * 群最后消息的更新时间（热点群不需要写扩散，更新这里就行）
     */
    private LocalDateTime activeTime;

    /**
     * 最后一条消息id
     */
    private Long lastMsgId;

    /**
     * 额外信息（根据不同类型房间有不同存储的东西）
     */
    private String extJson;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;

	protected Long createBy;

	protected Boolean isDel;

	protected Long updateBy;

	protected Long tenantId;
}
