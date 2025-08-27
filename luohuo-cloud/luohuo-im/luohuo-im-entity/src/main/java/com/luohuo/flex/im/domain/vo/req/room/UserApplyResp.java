package com.luohuo.flex.im.domain.vo.req.room;

import com.luohuo.flex.im.domain.enums.ApplyStatusEnum;
import com.luohuo.flex.im.domain.enums.RoomTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserApplyResp implements Serializable {
	@Schema(description ="申请人uid")
    private Long uid;
	/**
	 * @see RoomTypeEnum
	 */
	@Schema(description ="申请类型 1 群 2加好友")
	private Integer type;
	@Schema(description ="type = 2 才需要房间id")
	private Long roomId;
	@Schema(description ="接收人uid, 管理员id")
	private Long targetId;
	@Schema(description ="申请消息")
    private String msg;
	/**
	 * @see ApplyStatusEnum
	 */
	@Schema(description ="申请状态")
	private Integer status;
	@Schema(description ="阅读状态 1未读 2已读")
	private Integer readStatus;
	@Schema(description ="主动申请加群 true 是主动申请进群")
	private Boolean applyFor;
	@Schema(description ="申请时间")
    private LocalDateTime createTime;
}