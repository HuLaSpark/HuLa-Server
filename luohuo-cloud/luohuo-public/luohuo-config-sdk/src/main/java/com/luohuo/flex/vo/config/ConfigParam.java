package com.luohuo.flex.vo.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.luohuo.flex.model.vo.query.OperParam;

/**
 * 配置查询参数
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ConfigParam extends OperParam {
	private static final long serialVersionUID = 1L;

	@NotBlank(message = "参数名称不能为空")
	@Size(max = 100, message = "参数名称不能超过100个字符")
	private String configName;

	private String configType;

	@NotBlank(message = "参数键名长度不能为空")
	@Size(max = 100, message = "参数键名长度不能超过100个字符")
	private String configKey;

	@NotBlank(message = "参数键值不能为空")
	@Size(max = 500, message = "参数键值长度不能超过500个字符")
	private String configValue;

	private String type;

	private Long configId;

}
