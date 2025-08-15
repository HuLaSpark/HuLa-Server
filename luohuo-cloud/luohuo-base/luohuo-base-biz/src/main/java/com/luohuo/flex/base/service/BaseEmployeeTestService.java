package com.luohuo.flex.base.service;

import com.luohuo.basic.base.manager.SuperCacheManager;
import com.luohuo.flex.base.entity.user.BaseEmployee;

/**
 * @author tangyh
 * @version v1.0
 * @date 2022/9/20 11:31 AM
 * @create [2022/9/20 11:31 AM ] [tangyh] [初始创建]
 */
public interface BaseEmployeeTestService extends SuperCacheManager<BaseEmployee> {
    /**
     * 单体查询
     *
     * @param id id
     * @return com.luohuo.flex.base.entity.user.BaseEmployee
     * @author tangyh
     * @date 2022/10/28 9:20 AM
     * @create [2022/10/28 9:20 AM ] [tangyh] [初始创建]
     */
    BaseEmployee get(Long id);
}
