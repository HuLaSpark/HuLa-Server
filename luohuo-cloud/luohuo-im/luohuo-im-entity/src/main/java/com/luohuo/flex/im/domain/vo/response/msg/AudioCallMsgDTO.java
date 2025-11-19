package com.luohuo.flex.im.domain.vo.response.msg;

import com.luohuo.flex.model.enums.CallStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 音频消息扩展类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AudioCallMsgDTO implements Serializable {
    private static final long serialVersionUID = 1L;
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
    // 发起人UID
    private Long creator;
	// 是否是群聊通话
	private Boolean isGroup;
}