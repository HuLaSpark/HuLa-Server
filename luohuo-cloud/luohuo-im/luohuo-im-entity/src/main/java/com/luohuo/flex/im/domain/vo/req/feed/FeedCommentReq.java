package com.luohuo.flex.im.domain.vo.req.feed;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 朋友圈评论请求
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeedCommentReq {

	@NotNull(message = "朋友圈ID不能为空")
	@Schema(description = "朋友圈ID")
	private Long feedId;

	@Schema(description = "回复的评论ID（如果是回复评论，则有值；如果是直接评论朋友圈，则为null）")
	private Long replyCommentId;

	@Schema(description = "被回复人的uid（如果是回复评论，则有值）")
	private Long replyUid;

	@NotNull(message = "评论内容不能为空")
	@Size(min = 1, max = 500, message = "评论内容必须在1到500个字符之间")
	@Schema(description = "评论内容")
	private String content;
}
