package com.luohuo.flex.im.domain.vo.resp.feed;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 朋友圈点赞响应VO
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeedLikeVo {

    @Schema(description = "用户ID")
    private Long uid;

    @Schema(description = "用户昵称")
    private String userName;

    @Schema(description = "用户头像")
    private String userAvatar;
}
