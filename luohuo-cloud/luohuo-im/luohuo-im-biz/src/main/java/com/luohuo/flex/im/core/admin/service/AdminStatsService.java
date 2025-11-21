package com.luohuo.flex.im.core.admin.service;

import com.luohuo.flex.im.domain.vo.resp.admin.AdminStatsResp;

/**
 * 后台管理统计服务
 * @author 乾乾
 */
public interface AdminStatsService {

    /**
     * 获取首页统计数据
     * @return 统计数据
     */
    AdminStatsResp getHomeStats();
}
