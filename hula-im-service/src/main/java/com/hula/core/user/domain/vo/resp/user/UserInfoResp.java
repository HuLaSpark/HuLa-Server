package com.hula.core.user.domain.vo.resp.user;

import com.hula.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.Date;

/**
 * @author nyh
 */
@Data
public class UserInfoResp extends BaseEntity {

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
    private Date avatarUpdateTime;

	@Schema(description = "是否开启上下文[AI模块]")
	private Boolean context;

	@Schema(description = "调用次数[AI模块]")
	private Integer num;
}
