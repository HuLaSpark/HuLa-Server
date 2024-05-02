package com.hula.core.user.domain.vo.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author nyh
 */
@Data
public class UserInfoResp {
    @Schema(description = "用户id")
    private Long id;
    @Schema(description = "用户名")
    private String name;
    @Schema(description = "头像")
    private String avatar;
    @Schema(description = "性别 1男 2女")
    private Integer sex;
    @Schema(description = "修改昵称次数")
    private Integer modifyNameChance;
}
