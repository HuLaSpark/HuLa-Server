package com.luohuo.flex.base.mapper.user;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import com.luohuo.basic.base.mapper.SuperMapper;
import com.luohuo.flex.base.entity.user.BaseOrg;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * 组织
 * </p>
 *
 * @author 乾乾
 * @date 2021-10-18
 */
@Repository
public interface BaseOrgMapper extends SuperMapper<BaseOrg> {


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
