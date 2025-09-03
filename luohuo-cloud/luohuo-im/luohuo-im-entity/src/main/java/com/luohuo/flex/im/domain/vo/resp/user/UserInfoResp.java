package com.luohuo.flex.im.domain.vo.resp.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author nyh
 */
@Data
public class UserInfoResp implements Serializable {

    @Schema(description = "用户id")
    private Long uid;

    @Schema(description = "Hula号")
    private String account;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "用户昵称")
    private String name;

    @Schema(description = "头像")
    private String avatar;

    @Schema(description = "性别 1男 2女")
    private Integer sex;

    @Schema(description = "用户状态id")
    private Long userStateId;

    @Schema(description = "修改昵称次数")
    private Integer modifyNameChance;

    @Schema(description = "头像更换时间")
    private LocalDateTime avatarUpdateTime;

	@Schema(description = "是否开启上下文[AI模块]")
	private Boolean context;

	@Schema(description = "调用次数[AI模块]")
	private Integer num;
}
