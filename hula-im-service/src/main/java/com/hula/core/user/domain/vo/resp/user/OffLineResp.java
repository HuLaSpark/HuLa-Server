package com.hula.core.user.domain.vo.resp.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 用户同时在同一个设备登录，踢下线另一个设备
 * @author 乾乾
 * @date 2025年02月21日16:18:18
 */
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@Schema(description = "登录结果")
public class OffLineResp {
	@Schema(description = "用户id")
	private Long uid;

	@Schema(description = "客户端")
	private String client;

	@Schema(description = "登录ip")
	private String ip;

	public OffLineResp() {
	}

	public OffLineResp(Long uid, String client, String ip) {
		this.ip = ip;
		this.uid = uid;
		this.client = client;
	}
}
