package com.hula.core.user.service;

/**
 * @author nyh
 */
public interface IpService {
    /**
     * 异步更新用户ip详情
     *
     * @param uid 用户id
     */
    void refreshIpDetailAsync(Long uid);
}
