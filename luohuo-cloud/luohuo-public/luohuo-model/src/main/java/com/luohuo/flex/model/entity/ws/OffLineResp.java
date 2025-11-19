package com.luohuo.flex.model.entity.ws;

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
@Schema(description = "下线结果")
public class OffLineResp {
	@Schema(description = "用户id")
	private Long uid;

	@Schema(description = "客户端")
	private String client;

	@Schema(description = "客户端指纹")
	private String clientId;

	@Schema(description = "登录ip")
	private String ip;

	@Schema(description = "本次登录uuid (不需要被清空)")
	private String uuid;

	public OffLineResp() {
	}

	public OffLineResp(Long uid, String client, String clientId, String ip, String uuid) {
		this.ip = ip;
		this.uid = uid;
		this.client = client;
		this.clientId = clientId;
		this.uuid = uuid;
	}
}
