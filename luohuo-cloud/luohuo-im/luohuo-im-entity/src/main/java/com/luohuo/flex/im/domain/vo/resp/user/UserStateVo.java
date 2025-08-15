package com.luohuo.flex.im.domain.vo.resp.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserStateVo implements Serializable {

	@Schema(description = "更改人id")
	private String uid;

	@Schema(description = "状态id")
	private String userStateId;

	public UserStateVo() {
	}

	public UserStateVo(Long uid, Long stateId) {
		this.uid = String.valueOf(uid);
		this.userStateId = String.valueOf(stateId);
	}
}
