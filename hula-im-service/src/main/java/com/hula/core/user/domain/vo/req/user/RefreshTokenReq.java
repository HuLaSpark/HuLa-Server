package com.hula.core.user.domain.vo.req.user;

import com.hula.domain.BaseEntity;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

/**
 * @author 乾乾
 */
@Data
public class RefreshTokenReq extends BaseEntity {

    @NotEmpty(message = "refreshToken不能为空")
    private String refreshToken;
}
