package com.hula.core.user.domain.vo.req.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * @author nyh
 */
@Data
public class BlackReq {

    @NotNull
    @Schema(description = "拉黑用户的uid")
    private Long uid;
}
