package com.luohuo.flex.im.core.user.service;

import com.luohuo.flex.im.domain.enums.RoleTypeEnum;

/**
 * <p>
 * 角色表 服务类
 * </p>
 *
 * @author nyh
 */
public interface RoleService {
    /**
     * 判断用户是否有权限
     **/
    boolean hasRole(Long uid, RoleTypeEnum roleTypeEnum);
}
