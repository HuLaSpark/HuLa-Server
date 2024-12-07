package com.hula.core.user.service.impl;

import com.hula.core.user.domain.enums.RoleTypeEnum;
import com.hula.core.user.service.RoleService;
import com.hula.core.user.service.cache.UserCache;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

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
