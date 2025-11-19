package com.luohuo.flex.oauth.vo.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import com.luohuo.flex.oauth.enumeration.GrantType;

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
@Builder
@Schema(description = "登录参数")
public class LoginParamVO {
    @Schema(description = "验证码KEY")
    private String key;

    @Schema(description = "验证码")
    private String code;

	@Schema(description = "用户名")
	private String account;

    @Schema(description = "密码")
    private String password;

    @Schema(description = "手机号")
    private String mobile;

	@Schema(description = "客户端指纹信息")
	@NotNull(message = "设备指纹信息异常")
	private String clientId = "";

	/**
	 * @see com.luohuo.flex.oauth.emuns.LoginEnum
	 */
	@Schema(description = "登录类型 1-账号密码登录，2-IM聊天系统登录")
	@NotNull(message = "请选择登录系统")
	private Integer systemType;

	@Pattern(regexp = "^(PC|MOBILE)$", message = "登录方式只能是 PC 或 MOBILE")
	@NotEmpty(message = "登录方式只能是 PC 或 MOBILE")
	@Schema(description = "请选择登录方式 PC MOBILE")
	private String deviceType;

    /**
     * password: 账号密码
     * refresh_token: 刷新token
     * captcha: 验证码
     */
    @Schema(description = "授权类型", example = "CAPTCHA", allowableValues = "CAPTCHA,REFRESH_TOKEN,PASSWORD,MOBILE")
    @NotNull(message = "授权类型不能为空")
    private GrantType grantType;

    /**
     * 前端界面点击清空缓存时调用
     */
    @Schema(description = "刷新token")
    private String refreshToken;
}
