package com.luohuo.flex.im.domain.vo.req.user;

import com.luohuo.basic.base.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * TODO 待删除
 * @author ZOL
 */
@Data
public class RegisterReq extends BaseEntity {

	@NotEmpty(message = "请输入昵称")
    private String name;

    @NotNull
    @Pattern(regexp = "00[1-9]|01[0-9]|021", message = "默认头像只能是001到021之间的字符串")
    private String avatar;

	@NotEmpty(message = "请填写邮箱")
	@Schema(description = "邮箱")
	@Pattern(regexp = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$", message = "请输入有效的邮箱地址")
	private String email;

	@NotEmpty(message = "邮箱验证码不能为空")
	@Schema(description = "邮箱验证码")
	private String code;

	@NotEmpty(message = "注册码非法!")
	@Schema(description = "uuid")
	private String uuid;

    @NotEmpty(message = "请输入密码")
    private String password;

	private String openId;

	public RegisterReq() {
	}

	public RegisterReq(String name, String avatar, String email, String password, String openId) {
		this.name = name;
		this.avatar = avatar;
		this.email = email;
		this.password = password;
		this.openId = openId;
	}
}
