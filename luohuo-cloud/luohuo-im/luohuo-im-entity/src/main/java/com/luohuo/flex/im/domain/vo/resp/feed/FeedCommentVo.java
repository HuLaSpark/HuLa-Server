package com.luohuo.flex.im.domain.vo.resp.feed;

import com.luohuo.flex.im.domain.entity.FeedComment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 朋友圈评论响应VO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FeedCommentVo extends FeedComment {

    @Schema(description = "评论人昵称")
    private String userName;

    @Schema(description = "评论人头像")
    private String userAvatar;

    @Schema(description = "被回复人昵称")
    private String replyUserName;
}
