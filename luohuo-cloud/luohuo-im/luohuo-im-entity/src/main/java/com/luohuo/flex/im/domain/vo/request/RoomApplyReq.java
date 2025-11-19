package com.luohuo.flex.im.domain.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Description: 申请好友信息
 * Date: 2023-03-23
 */
@Data
public class RoomApplyReq {
    @Schema(description = "申请信息")
    private String msg = "申请加入群聊";

	@NotNull(message = "请输入群聊账号")
	@Schema(description = "群聊账号")
	private String account;

	@Schema(description = "1: 搜索进群 2: 扫码进群")
	private Integer type = 1;
}
