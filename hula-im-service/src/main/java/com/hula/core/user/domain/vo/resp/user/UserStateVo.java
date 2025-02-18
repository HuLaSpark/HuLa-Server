package com.hula.core.user.domain.vo.resp.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserStateVo implements Serializable {

	@Schema(description = "更改人id")
	private Long id;

	@Schema(description = "状态名")
	private String title;

	@Schema(description = "状态图标")
	private String url;

	public UserStateVo() {
	}

	public UserStateVo(Long id, String title, String url) {
		this.id = id;
		this.title = title;
		this.url = url;
	}
}
