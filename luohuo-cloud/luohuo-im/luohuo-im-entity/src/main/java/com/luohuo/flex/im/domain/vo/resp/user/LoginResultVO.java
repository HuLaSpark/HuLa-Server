package com.luohuo.flex.im.domain.vo.resp.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;


/**
 * 登录参数, 双token, 续签同时刷新 token 与 refreshToken; 相当于自动重新登录; 这样前端无法拿同一个 refreshToken 无限刷新
 *
 * @author 乾乾
 * @date 2025年02月19日22:18:12
 */
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description = "登录结果")
public class LoginResultVO {
	@Schema(description = "登录码")
	private String uuid;

    @Schema(description = "token")
    private String token;

    @Schema(description = "刷新token")
    private String refreshToken;

	@Schema(description = "客户端")
	private String client;

	public LoginResultVO() {
	}

	public LoginResultVO(String token, String refreshToken, String client) {
		this.token = token;
		this.refreshToken = refreshToken;
		this.client = client;
	}
}
