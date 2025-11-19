package com.luohuo.flex.im.domain.vo.req.feed;

import com.luohuo.flex.im.domain.entity.Feed;
import com.luohuo.flex.im.domain.vo.resp.feed.FeedLikeVo;
import com.luohuo.flex.im.domain.vo.resp.feed.FeedCommentVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 发布朋友圈的返回体
 */
@Data
public class FeedVo extends Feed {

	@Schema(description = "发布的内容的url")
	private List<String> urls;

	@Schema(description = "点赞数量")
	private Integer likeCount;

	@Schema(description = "评论数量")
	private Integer commentCount;

	@Schema(description = "当前用户是否已点赞")
	private Boolean hasLiked;

	@Schema(description = "点赞用户列表（列表页显示前3个，详情页显示全部）")
	private List<FeedLikeVo> likeList;

	@Schema(description = "评论列表（列表页显示前3条，详情页显示全部）")
	private List<FeedCommentVo> commentList;

	@Schema(description = "发布人昵称")
	private String userName;

	@Schema(description = "发布人头像")
	private String userAvatar;
}
