package com.hula.core.user.domain.vo.req.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author nyh
 */
@Data
public class LoginReq {

    @NotNull
    private String name;

    @NotNull
    private String password;
}
