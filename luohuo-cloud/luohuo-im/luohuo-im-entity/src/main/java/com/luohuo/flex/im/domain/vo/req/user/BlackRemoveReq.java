package com.luohuo.flex.im.domain.vo.req.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 移除黑名单请求
 * @author nyh
 */
@Data
public class BlackRemoveReq implements Serializable {

    @NotNull(message = "黑名单ID不能为空")
    @Schema(description = "黑名单ID")
    private Long id;
}
