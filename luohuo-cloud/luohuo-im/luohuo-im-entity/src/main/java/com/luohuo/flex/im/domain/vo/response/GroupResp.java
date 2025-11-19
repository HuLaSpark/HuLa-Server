package com.luohuo.flex.im.domain.vo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author 乾乾
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupResp implements Serializable {
	@Schema(description ="群聊id")
	private Long groupId;
    @Schema(description ="房间id")
    private Long roomId;
    @Schema(description ="群名称")
    private String name;
    @Schema(description ="群头像")
    private String avatar;
	@Schema(description = "群号")
	private String account;
	@Schema(description = "群备注")
	private String remark;
}
