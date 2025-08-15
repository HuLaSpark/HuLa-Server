package com.luohuo.flex.im.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.luohuo.basic.base.entity.Entity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@TableName("im_user_state")
public class UserState extends Entity<Long> {
	@Schema(description = "状态名")
	private String title;

	@Schema(description = "状态图标")
	private String url;
}
