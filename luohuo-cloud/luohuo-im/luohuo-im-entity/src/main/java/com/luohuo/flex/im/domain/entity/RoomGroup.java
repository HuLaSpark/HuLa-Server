package com.luohuo.flex.im.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.luohuo.basic.base.entity.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 群聊房间表
 * </p>
 *
 * @author nyh
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("im_room_group")
public class RoomGroup extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 房间id
     */
    @TableField("room_id")
    private Long roomId;

    /**
     * 群名称
     */
    @TableField("name")
    private String name;

	/**
	 * Hula群号
	 */
	@TableField("account")
	private String account;

    /**
     * 群头像
     */
    @TableField("avatar")
    private String avatar;

    /**
     * 额外信息（根据不同类型房间有不同存储的东西）
     */
    @TableField("ext_json")
    private String extJson;

	/**
	 * 允许扫码直接进群
	 */
	@TableField("allow_scan_enter")
	private Boolean allowScanEnter;

}
