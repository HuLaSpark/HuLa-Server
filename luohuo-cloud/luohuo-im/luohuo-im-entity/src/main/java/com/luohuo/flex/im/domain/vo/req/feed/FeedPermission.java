package com.luohuo.flex.im.domain.vo.req.feed;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import com.luohuo.flex.im.domain.entity.Feed;

import java.util.List;

/**
 * 发布朋友圈的返回体
 */
@Data
public class FeedPermission extends Feed {

	@Schema(description = "permission 限制的用户id")
	private List<Long> uidList;

	@Schema(description = "permission 限制的标签id")
	private List<Long> targetIds;

	public FeedPermission() {
	}

	public FeedPermission(List<Long> uidList, List<Long> targetIds) {
		this.uidList = uidList;
		this.targetIds = targetIds;
	}
}
