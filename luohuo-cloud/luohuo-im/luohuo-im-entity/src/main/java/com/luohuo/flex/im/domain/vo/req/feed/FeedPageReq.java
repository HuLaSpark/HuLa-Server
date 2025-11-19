package com.luohuo.flex.im.domain.vo.req.feed;

import com.luohuo.flex.im.domain.vo.req.CursorPageBaseReq;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "朋友圈分页查询请求")
public class FeedPageReq extends CursorPageBaseReq {

    @Schema(description = "用户昵称搜索关键词")
    private String userName;
}
