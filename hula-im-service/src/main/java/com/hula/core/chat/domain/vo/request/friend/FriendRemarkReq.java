package com.hula.core.chat.domain.vo.request.friend;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Description: 修改好友备注
 * Date: 2025-02-19
 */
@Data
public class FriendRemarkReq {
	public FriendRemarkReq() {
	}

	public FriendRemarkReq(Long targetUid, String remark) {
		this.targetUid = targetUid;
		this.remark = remark;
	}

    @NotNull(message = "请选择好友")
    @Schema(description = "好友uid")
    private Long targetUid;

	@NotBlank(message = "好友备注不能是空")
	@Schema(description = "好友备注")
	private String remark;
}
