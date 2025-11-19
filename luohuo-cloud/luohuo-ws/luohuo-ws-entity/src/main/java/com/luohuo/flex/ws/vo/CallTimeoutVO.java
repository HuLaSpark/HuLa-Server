package com.luohuo.flex.ws.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CallTimeoutVO {
    private String targetUid; // 未接听的用户ID

	public CallTimeoutVO(Long targetUid) {
		this.targetUid = targetUid + "";
	}
}