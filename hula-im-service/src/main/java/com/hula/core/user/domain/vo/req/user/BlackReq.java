package com.hula.core.user.domain.vo.req.user;

import com.hula.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * @author nyh
 */
@Data
public class BlackReq extends BaseEntity {

    @NotNull
    @Schema(description = "拉黑用户的uid")
    private Long uid;
}
