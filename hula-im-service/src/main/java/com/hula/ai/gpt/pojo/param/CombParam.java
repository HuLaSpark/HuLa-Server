package com.hula.ai.gpt.pojo.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 会员套餐 参数
 *
 * @author: 云裂痕
 * @date: 2025-03-06
 * 得其道 乾乾
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CombParam implements Serializable {

    private static final long serialVersionUID = 1L;

	@Schema(description = "页码")
	private Integer current;

	@Schema(description = "分页大小")
	private Integer size;

	private String title;

	@Schema(description = "分类")
	private Integer type;

	@Schema(description = "状态")
	private Integer status;
}
