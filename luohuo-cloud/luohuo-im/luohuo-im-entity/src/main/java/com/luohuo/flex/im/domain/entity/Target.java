package com.luohuo.flex.im.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.luohuo.basic.base.entity.Entity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@TableName("im_target")
public class Target extends Entity<Long> {

	@Schema(description = "标签创建人")
	private Long uid;

	private String name;

	private String icon;
}
