package com.luohuo.flex.im.domain.vo.req.room;

import com.luohuo.flex.im.domain.vo.req.PageBaseReq;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 群聊分页查询请求
 * @author nyh
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "群聊分页查询请求")
public class GroupPageReq extends PageBaseReq implements Serializable {

    @Schema(description = "群昵称搜索关键词")
    private String groupNameKeyword;

    @Schema(description = "群成员昵称搜索关键词")
    private String memberNameKeyword;
}
