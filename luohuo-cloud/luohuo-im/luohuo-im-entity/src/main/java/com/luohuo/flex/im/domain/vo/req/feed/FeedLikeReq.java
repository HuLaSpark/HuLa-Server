package com.luohuo.flex.im.domain.vo.req.feed;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 朋友圈点赞请求
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeedLikeReq {

	@NotNull(message = "朋友圈ID不能为空")
	@Schema(description = "朋友圈ID")
	private Long feedId;

	@NotNull(message = "操作类型不能为空")
	@Schema(description = "操作类型 1-点赞 2-取消点赞")
	private Integer actType;
}
