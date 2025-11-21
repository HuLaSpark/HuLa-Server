package com.luohuo.flex.im.domain.vo.req.room;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 管理员修改群成员昵称请求
 */
@Data
@Schema(description = "管理员修改群成员昵称请求")
public class UpdateMemberNicknameReq implements Serializable {

	@NotNull(message = "房间id")
	@Schema(description = "房间id")
	private Long roomId;

    @NotNull(message = "用户ID不能为空")
    @Schema(description = "用户ID")
    private Long uid;

    @Schema(description = "群昵称（我在群里面的名称）")
    @Size(max = 12, message = "群昵称长度必须在0到12个字符之间")
    private String myName;

    @Schema(description = "群备注")
    @Size(max = 10, message = "群备注长度必须在0到10个字符之间")
    private String remark;
}
