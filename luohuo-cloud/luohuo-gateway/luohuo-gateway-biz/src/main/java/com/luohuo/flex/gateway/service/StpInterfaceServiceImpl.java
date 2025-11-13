package com.luohuo.flex.gateway.service;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

import static com.luohuo.basic.context.ContextConstants.JWT_KEY_PERMISSION_LIST;
import static com.luohuo.basic.context.ContextConstants.JWT_KEY_ROLE_LIST;

/**
 * sa-token 权限网关实现
 * Gateway 是响应式的，不能依赖 luohuo-oauth-biz（Servlet）、所以直接从 Session 中读取权限列表（登录时已经存入）
 *
 * @author 乾乾
 * @since 2025/11/13 21:46
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class StpInterfaceServiceImpl implements StpInterface {

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        try {
            SaSession tokenSession = StpUtil.getTokenSession();
			return tokenSession.get(JWT_KEY_PERMISSION_LIST, Collections.emptyList());
        } catch (Exception e) {
            log.error("获取权限列表失败，loginId={}", loginId, e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        try {
            SaSession tokenSession = StpUtil.getTokenSession();
			return tokenSession.get(JWT_KEY_ROLE_LIST, Collections.emptyList());
        } catch (Exception e) {
            log.error("获取角色列表失败，loginId={}", loginId, e);
            return Collections.emptyList();
        }
    }
}
