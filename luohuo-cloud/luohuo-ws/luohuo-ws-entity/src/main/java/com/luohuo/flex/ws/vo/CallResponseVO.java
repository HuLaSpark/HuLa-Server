package com.luohuo.flex.ws.vo;

import lombok.Data;

@Data
public class CallResponseVO {
    private Long callerUid;
    private Long roomId;
	// -1 = 超时 0 = 拒绝 1 = 接通 2 = 挂断
    private Integer accepted;
}