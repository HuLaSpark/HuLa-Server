package com.luohuo.flex.im.domain.vo.resp.friend;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 好友校验
 * @author 乾乾
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendUnreadDto implements Serializable {

	@Schema(description ="类型")
	private Integer type;

	@Schema(description ="数量")
	private Integer count;
}
