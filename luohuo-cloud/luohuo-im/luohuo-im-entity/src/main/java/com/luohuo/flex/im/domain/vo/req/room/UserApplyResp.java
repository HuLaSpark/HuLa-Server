package com.luohuo.flex.im.domain.vo.req.room;

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
	@Schema(description ="申请类型 1加好友 2申请进群")
	private Integer type;
	@Schema(description ="type = 2 才需要房间id")
	private Long roomId;
	@Schema(description ="接收人uid, 管理员id")
	private Long targetId;
	@Schema(description ="申请消息")
    private String msg;
	@Schema(description ="申请状态 1待审批 2同意 3拒绝")
	private Integer status;
	@Schema(description ="阅读状态 1未读 2已读")
	private Integer readStatus;
	@Schema(description ="申请时间")
    private LocalDateTime createTime;
}