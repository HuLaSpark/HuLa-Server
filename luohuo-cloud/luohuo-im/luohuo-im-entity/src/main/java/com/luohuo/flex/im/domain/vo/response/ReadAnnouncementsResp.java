package com.luohuo.flex.im.domain.vo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 已读信息
 */
@Data
public class ReadAnnouncementsResp implements Serializable {

	@Schema(description = "此次已读人员")
	private Long uid;

	@Schema(description = "已读数量")
	private Long count;

	public ReadAnnouncementsResp() {
	}

	public ReadAnnouncementsResp(Long uid, Long count) {
		this.uid = uid;
		this.count = count;
	}
}
