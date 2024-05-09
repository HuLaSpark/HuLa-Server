package com.hula.core.user.service;

import com.hula.core.user.domain.enums.RoleEnum;

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
    boolean hasPower(Long uid, RoleEnum roleEnum);
}
