package com.hula.core.user.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("user_target_rel")
public class UserTargetRel implements Serializable {

	@TableId(value = "id", type = IdType.AUTO)
	private Long id;

	@Schema(description = "自己的id")
	private Long uid;

	@Schema(description = "关联的好友id")
	private Long friendId;

	@Schema(description = "标签id")
	private Long targetId;
}
