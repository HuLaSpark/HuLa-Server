package com.luohuo.flex.model.vo.query;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 绑定邮箱
 * @author 乾乾
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BindEmailReq implements Serializable {

	@NotEmpty(message = "请填写邮箱")
    @Schema(description = "邮箱")
    private String email;

	@NotEmpty(message = "验证码不能为空")
	@Schema(description = "验证码")
	private String code;

	@Schema(description = "指纹信息 [配合验证码使用的]")
	private String clientId;

	@Schema(description = "uuid")
	private String uuid;

	@Schema(description = "操作类型")
	private String operationType = "register";

	@NotEmpty
	private String templateCode;
}
