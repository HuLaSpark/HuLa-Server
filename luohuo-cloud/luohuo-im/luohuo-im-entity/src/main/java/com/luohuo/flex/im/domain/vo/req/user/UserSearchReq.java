package com.luohuo.flex.im.domain.vo.req.user;

import com.luohuo.flex.im.domain.vo.req.PageBaseReq;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户搜索请求
 * @author nyh
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserSearchReq extends PageBaseReq {

    @Schema(description = "搜索关键词（昵称模糊查询）")
    private String keyword;

    @Schema(description = "用户ID（精确查询，用于回显）")
    private Long id;
}
