package com.luohuo.flex.base.mapper.user;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import com.luohuo.basic.base.mapper.SuperMapper;
import com.luohuo.flex.base.entity.user.BaseEmployeeOrgRel;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * 员工所在部门
 * </p>
 *
 * @author 乾乾
 * @date 2021-10-18
 */
@Repository
public interface BaseEmployeeOrgRelMapper extends SuperMapper<BaseEmployeeOrgRel> {

    /**
     * 查询员工拥有的机构
     *
     * @param employeeId employeeId
     * @return java.util.List<java.lang.Long>
     * @author tangyh
     * @date 2022/10/20 3:44 PM
     * @create [2022/10/20 3:44 PM ] [tangyh] [初始创建]
     */
    List<Long> selectOrgByEmployeeId(@Param("employeeId") Long employeeId);
}
