package com.luohuo.flex.oauth.event.listener;

import cn.hutool.core.bean.BeanUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import com.luohuo.flex.base.service.tenant.DefUserService;
import com.luohuo.flex.oauth.event.LoginEvent;
import com.luohuo.flex.oauth.event.model.LoginStatusDTO;
import com.luohuo.flex.oauth.enumeration.system.LoginStatusEnum;
import com.luohuo.flex.base.service.system.DefLoginLogService;
import com.luohuo.flex.base.vo.save.system.DefLoginLogSaveVO;

/**
 * 登录事件监听，用于记录登录日志
 *
 * @author 乾乾
 * @date 2020年03月18日17:39:59
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class LoginListener {
    private final DefLoginLogService defLoginLogService;
    private final DefUserService defUserService;

    @Async
    @EventListener({LoginEvent.class})
    public void saveSysLog(LoginEvent event) {
        LoginStatusDTO loginStatus = (LoginStatusDTO) event.getSource();

        if (LoginStatusEnum.SUCCESS.eq(loginStatus.getStatus())) {
            // 重置错误次数 和 最后登录时间
            this.defUserService.resetPassErrorNum(loginStatus.getUserId());
        } else if (LoginStatusEnum.PASSWORD_ERROR.eq(loginStatus.getStatus())) {
            // 密码错误
            this.defUserService.incrPasswordErrorNumById(loginStatus.getUserId());
        }
        defLoginLogService.save(BeanUtil.toBean(loginStatus, DefLoginLogSaveVO.class));
    }

}
