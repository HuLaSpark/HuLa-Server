package com.luohuo.flex.oauth.vo.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 登录参数
 *
 * @author 乾乾
 * @date 2020年01月05日22:18:12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description = "注册")
public class RegisterVO {
    @Schema(description = "验证码KEY")
	@NotEmpty(message = "验证码KEY不能为空")
	@Pattern(regexp = "^(REGISTER_SMS|REGISTER_EMAIL|MOBILE_LOGIN|MOBILE_EDIT|EMAIL_EDIT|PASSWORD_EDIT)$", message = "验证码KEY必须是 REGISTER_SMS, REGISTER_EMAIL, MOBILE_LOGIN, MOBILE_EDIT, EMAIL_EDIT 或 PASSWORD_EDIT 之一")
    private String key;

    @Schema(description = "验证码")
    @NotEmpty(message = "请填写验证码")
    private String code;

    @Schema(description = "密码")
    @NotEmpty(message = "请填写密码")
    @Size(min = 6, max = 64, message = "密码长度不能小于{min}且超过{max}个字符")
    private String password;

    @Schema(description = "确认密码")
    @NotEmpty(message = "请填写确认密码")
    @Size(min = 6, max = 64, message = "密码长度不能小于{min}且超过{max}个字符")
    private String confirmPassword;

    @Schema(description = "昵称")
    private String nickName;

	@Schema(description = "头像")
	private String avatar;

	@NotNull(message = "请选择系统类型")
	private Integer systemType;
}
