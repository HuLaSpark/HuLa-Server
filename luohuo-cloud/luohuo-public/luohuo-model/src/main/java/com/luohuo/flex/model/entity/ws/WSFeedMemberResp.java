package com.luohuo.flex.model.entity.ws;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 朋友圈发布推送的消息
 */
@Data
public class WSFeedMemberResp {

	@Schema(description = "发布人")
	private Long uid;

	public WSFeedMemberResp() {
	}

	public WSFeedMemberResp(Long uid) {
		this.uid = uid;
	}
}
