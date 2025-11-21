package com.luohuo.flex.oauth.event;

import org.springframework.context.ApplicationEvent;
import com.luohuo.flex.oauth.event.model.LoginStatusDTO;

/**
 * 登录事件
 *
 * @author 乾乾
 * @date 2020年03月18日17:22:55
 */
public class LoginEvent extends ApplicationEvent {
    public LoginEvent(LoginStatusDTO source) {
        super(source);
    }
}
