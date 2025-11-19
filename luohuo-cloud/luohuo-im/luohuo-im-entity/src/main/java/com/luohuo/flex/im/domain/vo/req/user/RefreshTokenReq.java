package com.luohuo.flex.im.domain.vo.req.user;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 乾乾
 */
@Data
public class RefreshTokenReq implements Serializable {

    @NotEmpty(message = "refreshToken不能为空")
    private String refreshToken;
}
