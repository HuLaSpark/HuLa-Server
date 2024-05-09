package com.hula.core.user.domain.vo.resp.friend;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 好友校验
 * @author nyh
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendApplyResp {
    @Schema(description ="申请id")
    private Long applyId;

    @Schema(description ="申请人uid")
    private Long uid;

    @Schema(description ="申请类型 1加好友")
    private Integer type;

    @Schema(description ="申请信息")
    private String msg;

    @Schema(description ="申请状态 1待审批 2同意")
    private Integer status;
}
