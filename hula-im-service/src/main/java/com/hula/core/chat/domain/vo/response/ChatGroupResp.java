package com.hula.core.chat.domain.vo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Description: 群信息
 * Date: 2025-02-26
 */
@Data
public class ChatGroupResp {
    @Schema(description = "群id")
    private Long id;

	@Schema(description = "群名称")
    private String name;

    @Schema(description = "群头像")
    private String avatar;

	@Schema(description = "群账号")
	private String account;

	@Schema(description = "扩展数据")
	private String extJson;

	@Schema(description = "群人数")
	private Integer count;
}
