package com.luohuo.flex.base.satoken;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import com.luohuo.basic.utils.CollHelper;
import com.luohuo.basic.utils.StrPool;
import com.luohuo.flex.base.service.system.BaseRoleService;
import com.luohuo.flex.common.constant.RoleConstant;
import com.luohuo.flex.base.entity.application.DefResource;
import com.luohuo.flex.base.service.application.DefResourceService;

import java.util.Collections;
import java.util.List;

import static com.luohuo.basic.context.ContextConstants.JWT_KEY_U_ID;

/**
 * sa-token 权限实现
 *
 * @author tangyh
 * @since 2024/7/26 17:35
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class StpInterfaceImpl implements StpInterface {
    private final DefResourceService defResourceService;
    private final BaseRoleService baseRoleService;

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        SaSession tokenSession = StpUtil.getTokenSession();
        long uid = tokenSession.getLong(JWT_KEY_U_ID);
        // 超管 返回 *
        List<DefResource> list;
        boolean isAdmin = baseRoleService.checkRole(uid, RoleConstant.TENANT_ADMIN);
        List<String> resourceCodes = Collections.emptyList();
        if (isAdmin) {
            // 管理员 拥有所有权限
            list = List.of(DefResource.builder().code("*").build());
        } else {
            List<Long> resourceIdList = baseRoleService.findResourceIdByEmployeeId(null, uid);
            if (resourceIdList.isEmpty()) {
                return Collections.emptyList();
            }

            list = defResourceService.findByIdsAndType(resourceIdList, resourceCodes);
        }
        return CollHelper.split(list, DefResource::getCode, StrPool.SEMICOLON);
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        SaSession tokenSession = StpUtil.getTokenSession();
        long uid = tokenSession.getLong(JWT_KEY_U_ID);
        boolean isAdmin = baseRoleService.checkRole(uid, RoleConstant.TENANT_ADMIN);
        if (isAdmin) {
            return List.of("*");
        } else {
            return baseRoleService.findRoleCodeByEmployeeId(uid);
        }
    }
}
