package com.luohuo.flex.im.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.luohuo.basic.base.entity.SuperEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 朋友圈评论表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("im_feed_comment")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeedComment extends SuperEntity<Long> {

	private static final long serialVersionUID = 1L;

	/**
	 * 朋友圈ID
	 */
	@Schema(description = "朋友圈ID")
	@TableField("feed_id")
	private Long feedId;

	/**
	 * 评论人uid
	 */
	@Schema(description = "评论人uid")
	@TableField("uid")
	private Long uid;

	/**
	 * 回复的评论ID（如果是回复评论，则有值；如果是直接评论朋友圈，则为null）
	 */
	@Schema(description = "回复的评论ID")
	@TableField("reply_comment_id")
	private Long replyCommentId;

	/**
	 * 被回复人的uid（如果是回复评论，则有值）
	 */
	@Schema(description = "被回复人的uid")
	@TableField("reply_uid")
	private Long replyUid;

	/**
	 * 评论内容
	 */
	@Schema(description = "评论内容")
	@TableField("content")
	private String content;
}
