package com.hula.core.user.domain.vo.req.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import java.io.Serializable;

/**
 * @author 乾乾
 */
@Data
public class ForgotPasswordReq implements Serializable {

	@NotEmpty(message = "请填写邮箱")
	@Schema(description = "邮箱")
	@Pattern(regexp = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$", message = "请输入有效的邮箱地址")
	private String email;

	@NotEmpty(message = "邮箱验证码不能为空")
	@Schema(description = "邮箱验证码")
	private String code;

	@NotEmpty(message = "修改码非法!")
	@Schema(description = "uuid")
	private String uuid;

    @NotEmpty(message = "请输入密码")
    private String password;
}
