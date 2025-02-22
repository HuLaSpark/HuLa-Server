package com.hula.core.user.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("target")
public class Target implements Serializable {

	@TableId(value = "id", type = IdType.AUTO)
	private Long id;

	@Schema(description = "标签创建人")
	private Long uid;

	private String name;

	private String icon;
}
