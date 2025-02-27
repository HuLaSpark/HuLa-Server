package com.hula.core.user.domain.vo.req.user;

import com.hula.domain.BaseEntity;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * @author ZOL
 */
@Data
public class RegisterReq extends BaseEntity {

	@NotEmpty(message = "请输入昵称")
    private String name;

    @NotNull
    @Pattern(regexp = "00[1-9]|01[0-9]|021", message = "默认头像只能是001到021之间的字符串")
    private String avatar;

    @NotNull
    @Pattern(regexp = "[a-zA-Z0-9]{6,12}", message = "账号只能是字母或数字，6到12位")
    private String account;

    @NotEmpty(message = "请输入密码")
    private String password;

}
