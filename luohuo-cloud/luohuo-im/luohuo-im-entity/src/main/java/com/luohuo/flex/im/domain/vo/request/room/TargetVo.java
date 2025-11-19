package com.luohuo.flex.im.domain.vo.request.room;

import lombok.Data;

import java.io.Serializable;

@Data
public class TargetVo implements Serializable {

	private Long id;

	private String name;

	private String icon;
}
