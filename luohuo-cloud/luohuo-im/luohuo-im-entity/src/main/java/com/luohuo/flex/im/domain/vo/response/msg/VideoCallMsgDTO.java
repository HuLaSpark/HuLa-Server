package com.luohuo.flex.im.domain.vo.response.msg;

import com.luohuo.flex.model.enums.CallStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 视频消息扩展类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoCallMsgDTO implements Serializable {
    private static final long serialVersionUID = 1L;
	// 是否为发起通话的消息
	private Boolean begin;
    // 通话时长（秒）
    private Long duration;
	/**
	 * 通话状态
	 * @see CallStatusEnum
	 */
	private String state;
	// 开始时间
	private Long startTime;
	// 结束时间
	private Long endTime;
    // 操作人
    private Long creator;
	// 是否是群聊通话
	private Boolean isGroup;
}