package com.hula.core.user.service.impl;

import com.hula.core.user.domain.enums.RoleEnum;
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
    public boolean hasPower(Long uid, RoleEnum roleEnum) {
        Set<Long> roleSet = userCache.getRoleSet(uid);
        return isAdmin(roleSet) || roleSet.contains(roleEnum.getId());
    }

    private boolean isAdmin(Set<Long> roleSet) {
        return roleSet.contains(RoleEnum.ADMIN.getId());
    }
}
