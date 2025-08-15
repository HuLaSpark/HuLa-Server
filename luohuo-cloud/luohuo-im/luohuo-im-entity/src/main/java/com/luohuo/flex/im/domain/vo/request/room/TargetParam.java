package com.luohuo.flex.im.domain.vo.request.room;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 标签的添加参数
 */
@Data
public class TargetParam implements Serializable {

	private Long id;

	@Size(min = 1, max = 8, message = "标签长度必须在1到8个字符之间")
	private String name;

	private String icon;
}
