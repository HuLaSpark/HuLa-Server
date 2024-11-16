package com.hula.core.user.domain.vo.req.user;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author ZOL
 */
@Data
public class RegisterReq {

    /**
     * 用户昵称
     */
    @NotNull
    private String name;

    /**
     * 用户账号
     */
    @NotNull
    private String account;

    /**
     * 用户密码
     */
    @NotNull
    private String password;

}
