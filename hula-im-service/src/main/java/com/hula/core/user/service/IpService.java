package com.hula.core.user.service;

import com.hula.core.user.domain.entity.User;

/**
 * @author nyh
 */
public interface IpService {
    /**
     * 异步更新用户ip详情
     *
     * @param user 用户
     */
    void refreshIpDetailAsync(User user);
}
