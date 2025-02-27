package com.hula.core.user.domain.vo.req.user;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.io.Serializable;

/**
 * @author nyh
 */
@Data
public class LoginReq implements Serializable {

	@NotEmpty(message = "请输入账号")
    private String account;

	@NotEmpty(message = "请输入密码")
    private String password;

	@NotEmpty(message = "请选择登录方式")
	private String source;
}
