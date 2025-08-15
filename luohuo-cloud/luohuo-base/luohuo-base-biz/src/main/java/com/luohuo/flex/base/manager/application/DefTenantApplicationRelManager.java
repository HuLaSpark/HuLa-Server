package com.luohuo.flex.base.manager.application;

import com.luohuo.basic.base.manager.SuperManager;
import com.luohuo.flex.base.entity.application.DefTenantApplicationRel;

import java.util.List;

/**
 * <p>
 * 通用业务层
 * 租户的应用
 * </p>
 *
 */
public interface DefTenantApplicationRelManager extends SuperManager<DefTenantApplicationRel> {
    /**
     * 给指定租户授权 公共应用
     *
     * @param tenantId 租户id
     */
    void grantGeneralApplication(Long tenantId);


    /**
     * 查询员工拥有的应用
     *
     * @param employeeId 员工id
     * @return
     */
    List<Long> findApplicationByEmployeeId(Long employeeId);

    /**
     * 删除指定租户的应用
     *
     * @param ids ids
     * @author tangyh
     * @date 2022/10/28 4:54 PM
     * @create [2022/10/28 4:54 PM ] [tangyh] [初始创建]
     */
    void deleteByTenantId(List<Long> ids);
}
