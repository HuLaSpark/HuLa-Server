package com.luohuo.flex.im.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import com.luohuo.basic.base.entity.Entity;
import com.luohuo.flex.im.domain.enums.NoticeStatusEnum;
import com.luohuo.flex.im.domain.enums.RoomTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 用户申请表
 * </p>
 *
 * @author nyh
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("im_user_apply")
@AllArgsConstructor
@NoArgsConstructor
public class UserApply extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 申请人uid\邀请人
     */
    @TableField("uid")
    private Long uid;

    /**
	 * 申请类型
	 * @see RoomTypeEnum
     */
    @TableField("type")
    private Integer type;

	/**
	 * 房间id type = 2 才需要
	 */
	@TableField("room_id")
	private Long roomId;

    /**
     * 接收人uid\被邀请的uid
     */
    @TableField("target_id")
    private Long targetId;

    /**
     * 申请信息
     */
    @TableField("msg")
    private String msg;

    /**
	 * 申请状态
     * @see NoticeStatusEnum
     */
    @TableField("status")
    private Integer status;

    /**
	 * 阅读状态
	 * @see com.luohuo.flex.im.domain.enums.ApplyReadStatusEnum
     */
    @TableField("read_status")
    private Integer readStatus;

    /**
     * 删除状态 0：未删 1 申请人删除 2 被申请人删除 3都删除
     */
    @TableField("deleted")
    private Integer deleted;

	/**
	 * 主动申请加群
	 */
	@TableField("apply_for")
	private Boolean applyFor;

	/**
	 * 进群渠道 1-邀请 2-扫码 3-搜索
	 */
	@TableField("join_channel")
	private Integer joinChannel;
}
