package com.luohuo.flex.im.core.user.service.impl;

import com.luohuo.flex.im.core.user.service.cache.UserSummaryCache;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import com.luohuo.flex.im.domain.enums.RoleTypeEnum;
import com.luohuo.flex.im.core.user.service.RoleService;
import java.util.Set;

/**
 * @author nyh
 */
@Service
public class RoleServiceImpl implements RoleService {

    @Resource
    private UserSummaryCache userSummaryCache;

    @Override
    public boolean hasRole(Long uid, RoleTypeEnum roleTypeEnum) {
        Set<Long> roleSet = userSummaryCache.getRoleSet(uid);
        return isAdmin(roleSet) || roleSet.contains(roleTypeEnum.getId());
    }

    private boolean isAdmin(Set<Long> roleSet) {
        return roleSet.contains(RoleTypeEnum.ADMIN.getId());
    }
}
