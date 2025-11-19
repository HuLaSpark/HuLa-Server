package com.luohuo.flex.ws.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CallAcceptedVO {
    private String acceptedBy; // 接受呼叫的用户ID
    private String roomId;     // 房间ID

	public CallAcceptedVO(Long acceptedBy, Long roomId) {
		this.acceptedBy = acceptedBy+"";
		this.roomId = roomId+"";
	}
}