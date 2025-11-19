package com.luohuo.flex.im.domain.vo.response.msg;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 消息撤回
 * @author nyh
 */
@Data
@Builder
@NoArgsConstructor
public class MsgRecallDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    //撤回消息的uid
    private Long recallUid;
    //撤回的时间点
	private Long recallTime;

	public MsgRecallDTO(Long recallUid, Long date) {
		this.recallUid = recallUid;
		this.recallTime = date;
	}
}
