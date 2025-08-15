package com.luohuo.flex.im.core.user.service.impl;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import com.luohuo.flex.im.domain.enums.RoleTypeEnum;
import com.luohuo.flex.im.core.user.service.RoleService;
import com.luohuo.flex.im.core.user.service.cache.UserCache;

import java.util.Set;

/**
 * @author nyh
 */
@Service
public class RoleServiceImpl implements RoleService {

    @Resource
    private UserCache userCache;

    @Override
    public boolean hasRole(Long uid, RoleTypeEnum roleTypeEnum) {
        Set<Long> roleSet = userCache.getRoleSet(uid);
        return isAdmin(roleSet) || roleSet.contains(roleTypeEnum.getId());
    }

    private boolean isAdmin(Set<Long> roleSet) {
        return roleSet.contains(RoleTypeEnum.ADMIN.getId());
    }
}
