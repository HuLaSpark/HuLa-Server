package com.luohuo.flex.im.api.vo;

import lombok.Data;

@Data
public class UserRegisterVo {
    /**
     * DefUser用户id
     */
    private Long userId;
    private String account;
    private String email;
    private String name;
    private String avatar;
    private Integer sex;

    private Long userStateId;
	private Long tenantId;
    private Integer userType;
}
