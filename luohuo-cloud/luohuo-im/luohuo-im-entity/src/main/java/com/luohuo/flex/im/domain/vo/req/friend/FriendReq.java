package com.luohuo.flex.im.domain.vo.req.friend;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

/**
 * Description: 查询好友
 * Date: 2025-02-26
 */
@Data
public class FriendReq {

    @NotEmpty(message = "请输入手机号或账号")
    private String key;
}
