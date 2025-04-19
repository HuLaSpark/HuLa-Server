package com.hula.core.chat.domain.entity.msg;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 消息撤回
 * @author nyh
 */
@Data
@Builder
@NoArgsConstructor
public class MsgRecall implements Serializable {
    private static final long serialVersionUID = 1L;
    //撤回消息的uid
    private Long recallUid;
    //撤回的时间点
    private Date recallTime;

	public MsgRecall(Long recallUid, Date date) {
		this.recallUid = recallUid;
		this.recallTime = date;
	}
}
