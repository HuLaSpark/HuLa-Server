package com.luohuo.flex.base.service.user;

import com.luohuo.basic.base.service.SuperService;
import com.luohuo.flex.base.entity.user.BaseEmployeeOrgRel;

import java.util.List;

/**
 * <p>
 * 业务接口
 * 员工所在部门
 * </p>
 *
 * @author 乾乾
 * @date 2021-10-18
 */
public interface BaseEmployeeOrgRelService extends SuperService<Long, BaseEmployeeOrgRel> {
    /**
     * 根据员工id查询员工的机构id
     *
     * @param employeeId 员工id
     * @return
     */
    List<Long> findOrgIdListByEmployeeId(Long employeeId);
}
