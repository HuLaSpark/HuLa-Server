package com.luohuo.flex.im.domain.vo.req.feed;

import com.luohuo.flex.im.domain.vo.req.CursorPageBaseReq;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 朋友圈评论分页请求
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class FeedCommentPageReq extends CursorPageBaseReq {

	@NotNull(message = "朋友圈ID不能为空")
	@Schema(description = "朋友圈ID")
	private Long feedId;
}
