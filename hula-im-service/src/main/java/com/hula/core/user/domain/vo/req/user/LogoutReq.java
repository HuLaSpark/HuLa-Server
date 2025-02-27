package com.hula.core.user.domain.vo.req.user;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.io.Serializable;

/**
 * @author 乾乾
 */
@Data
public class LogoutReq implements Serializable {

	@NotNull(message = "是否自动登录")
	private Boolean autoLogin;
}
