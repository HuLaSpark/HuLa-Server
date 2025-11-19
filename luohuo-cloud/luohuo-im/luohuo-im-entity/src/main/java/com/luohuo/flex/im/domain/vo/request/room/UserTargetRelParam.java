package com.luohuo.flex.im.domain.vo.request.room;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class UserTargetRelParam {

	@Schema(description = "关联的好友id")
	private Long friendId;

	private List<Long> targetIds;
}
