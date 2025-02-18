package com.hula.core.user.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("user_state")
public class UserState implements Serializable {

	@TableId(value = "id", type = IdType.AUTO)
	private Long id;

	@Schema(description = "状态名")
	private String title;

	@Schema(description = "状态图标")
	private String url;

	private Long createdBy;
	private LocalDateTime createdTime;
	private LocalDateTime updatedTime;
}
