package com.hula.ai.gpt.pojo.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 *  内容管理 参数
 *
 * @author: 云裂痕
 * @date: 2025-03-06
 * 得其道 乾乾
 */
@Data
public class AgreementParam implements Serializable {

    private static final long serialVersionUID = 1L;

	@Schema(description = "页码")
	private Integer current;

	@Schema(description = "分页大小")
	private Integer size;

	@Schema(description = "状态")
    private Integer status;

	@Schema(description = "类型")
    private Integer type;

	@Schema(description = "标题")
	private String title;

	public AgreementParam() {
	}

	public AgreementParam(Integer status, Integer type) {
		this.status = status;
		this.type = type;
	}
}
