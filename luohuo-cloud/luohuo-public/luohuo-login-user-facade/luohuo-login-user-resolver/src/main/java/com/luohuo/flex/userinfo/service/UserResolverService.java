package com.luohuo.flex.userinfo.service;

import com.luohuo.basic.base.R;
import com.luohuo.flex.model.entity.system.SysUser;
import com.luohuo.flex.model.vo.result.UserQuery;

/**
 * @author 乾乾
 * @date 2020年02月24日10:41:49
 */
public interface UserResolverService {
    /**
     * 根据id查询用户
     *
     * @param userQuery 查询条件
     * @return 用户信息
     */
    R<SysUser> getById(UserQuery userQuery);
}
