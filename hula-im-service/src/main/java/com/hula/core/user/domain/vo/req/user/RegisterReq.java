package com.hula.core.user.domain.vo.req.user;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
    @Pattern(regexp = "[a-zA-Z0-9]{6,12}", message = "账号只能是字母或数字，6到12位")
    private String account;

    /**
     * 用户密码
     */
    @NotNull
    private String password;

}
