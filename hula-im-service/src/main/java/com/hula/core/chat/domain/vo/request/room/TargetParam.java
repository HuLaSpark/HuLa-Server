package com.hula.core.chat.domain.vo.request.room;

import lombok.Data;

import java.io.Serializable;

/**
 * 标签的添加参数
 */
@Data
public class TargetParam implements Serializable {

	private Long id;

	private String name;

	private String icon;
}
