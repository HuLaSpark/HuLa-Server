package com.luohuo.flex.base.vo.result.system;

import cn.dev33.satoken.session.SaSession;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 在线用户
 * @author tangyh
 * @since 2024/8/1 15:40
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class OnlineUsersResultVO extends SaSession {

    /**
     * 用户名;大小写数字下划线
     */
    private String username;

    /**
     * 姓名
     */
    private String nickName;
    /** 创建时间 */
    private LocalDateTime sessionTime;
    private String sessionStr;
    /**  失效时间 */
    private LocalDateTime expireTime;
    private String expireStr;
}
