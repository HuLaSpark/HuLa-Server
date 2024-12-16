package com.hula.core.user.domain.vo.req.user;

import com.hula.domain.BaseEntity;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author nyh
 */
@Data
public class LoginReq extends BaseEntity {

    @NotNull
    private String account;

    @NotNull
    private String password;
}
