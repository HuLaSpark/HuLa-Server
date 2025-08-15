package com.luohuo.flex.ws.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CallRejectedVO {
    private String rejectedBy; // 拒绝呼叫的用户ID

	public CallRejectedVO(Long rejectedBy) {
		this.rejectedBy = rejectedBy+"";
	}
}