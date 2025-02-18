package com.hula.core.user.domain.vo.resp.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserStateVo implements Serializable {

	@Schema(description = "更改人id")
	private Long uid;

	@Schema(description = "状态id")
	private Long stateId;

	public UserStateVo() {
	}

	public UserStateVo(Long uid, Long stateId) {
		this.uid = uid;
		this.stateId = stateId;
	}
}
