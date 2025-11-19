package com.luohuo.flex.im.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.luohuo.basic.base.entity.SuperEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@TableName("im_user_target_rel")
public class UserTargetRel extends SuperEntity<Long> {

	@Schema(description = "自己的id")
	private Long uid;

	@Schema(description = "关联的好友id")
	private Long friendId;

	@Schema(description = "标签id")
	private Long targetId;
}
