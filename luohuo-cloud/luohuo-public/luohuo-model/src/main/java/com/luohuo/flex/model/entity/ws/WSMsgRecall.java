package com.luohuo.flex.model.entity.ws;

import lombok.Data;
import java.io.Serializable;

/**
 * @author nyh
 */
@Data
public class WSMsgRecall implements Serializable {
	private String msgId;
	private String roomId;
	//撤回的用户
	private String recallUid;
}
