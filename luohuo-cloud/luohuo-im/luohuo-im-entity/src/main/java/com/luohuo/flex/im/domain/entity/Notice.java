package com.luohuo.flex.im.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.luohuo.basic.base.entity.Entity;
import com.luohuo.flex.im.domain.enums.NoticeStatusEnum;
import com.luohuo.flex.im.domain.enums.NoticeTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 统一通知表
 * </p>
 *
 * @author 乾乾
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("im_notice")
public class Notice extends Entity<Long> {

    private static final long serialVersionUID = 1L;

	/**
	 * @see NoticeTypeEnum
	 */
    @Schema(description = "通知类型:1-好友申请;2-群申请;3-群事件")
	private Integer eventType;

	@Schema(description = "通知类型 1群聊 2加好友")
	private Integer type;

    @Schema(description = "发起人UID")
    @TableField("sender_id")
    private Long senderId;

    @Schema(description = "接收人UID")
    @TableField("receiver_id")
    private Long receiverId;

    @Schema(description = "申请ID")
    @TableField("apply_id")
	private Long applyId;

	@Schema(description = "被操作的人")
	@TableField("operate_id")
	private Long operateId;

	@Schema(description = "房间id")
	private Long roomId;

	@Schema(description = "通知内容 申请时填写的, 进群、移除时是群聊的名称")
	private String content;

	/**
	 * @see NoticeStatusEnum
	 */
    @Schema(description = "处理状态:0-未处理;1-已同意;2-已拒绝;3-忽略")
    private Integer status;

    @Schema(description = "是否已读")
    @TableField("is_read")
    private Integer isRead;

    @Schema(description = "租户id")
    @TableField("tenant_id")
    private Long tenantId;
}