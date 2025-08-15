package com.luohuo.flex.im.domain.vo.request.friend;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * hideTheirPosts = hideMyPosts = true = 仅聊天
 * Description: 修改好友权限
 * Date: 2025-03-03
 */
@Data
public class FriendPermissionReq {
	@Schema(description = "好友id")
	private Long friendId;

	@Schema(description = "不让他看我（0-允许，1-禁止）")
	private Boolean hideMyPosts;

	@Schema(description = "不看他（0-允许，1-禁止）")
	private Boolean hideTheirPosts;
}
