package com.luohuo.flex.loginuser.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import com.luohuo.basic.base.R;
import com.luohuo.basic.constant.Constants;
import com.luohuo.flex.model.entity.base.SysEmployee;
import com.luohuo.flex.model.entity.base.SysOrg;
import com.luohuo.flex.model.entity.base.SysPosition;

import java.util.List;

/**
 * 基础服务
 *
 * @author tangyh
 * @version v1.0
 * @date 2022/9/29 11:05 PM
 * @create [2022/9/29 11:05 PM ] [tangyh] [初始创建]
 */
@FeignClient(name = "${" + Constants.PROJECT_PREFIX + ".feign.base-server:luohuo-base-server}")
public interface BaseApi {
    /**
     * 查询员工
     *
     * @param id 员工ID
     * @return com.luohuo.basic.base.R<com.luohuo.flex.model.entity.base.SysEmployee>
     * @author tangyh
     * @date 2022/11/18 2:26 PM
     * @create [2022/11/18 2:26 PM ] [tangyh] [初始创建]
     */
    @GetMapping("/baseEmployee/{id}")
    R<SysEmployee> getEmployeeById(@PathVariable Long id);

    /**
     * 查询机构信息
     *
     * @param id 机构
     * @return com.luohuo.basic.base.R<com.luohuo.flex.model.entity.base.SysOrg>
     * @author tangyh
     * @date 2022/11/18 2:26 PM
     * @create [2022/11/18 2:26 PM ] [tangyh] [初始创建]
     */
    @GetMapping("/baseOrg/{id}")
    R<SysOrg> getOrgById(@PathVariable Long id);

    /**
     * 查询岗位信息
     *
     * @param id 岗位ID
     * @return com.luohuo.basic.base.R<com.luohuo.flex.model.entity.base.SysPosition>
     * @author tangyh
     * @date 2022/11/18 2:26 PM
     * @create [2022/11/18 2:26 PM ] [tangyh] [初始创建]
     */
    @GetMapping("/basePosition/{id}")
    R<SysPosition> getPositionById(@PathVariable Long id);

    /**
     * 查询员工的角色编码
     *
     * @param employeeId 员工ID
     * @return com.luohuo.basic.base.R<java.util.List < java.lang.String>>
     * @author tangyh
     * @date 2022/11/18 2:26 PM
     * @create [2022/11/18 2:26 PM ] [tangyh] [初始创建]
     */
    @GetMapping("/baseRole/findRoleCodeByEmployeeId")
    R<List<String>> findRoleCodeByEmployeeId(@RequestParam("employeeId") Long employeeId);

    /**
     * 查询员工在指定公司下的部门
     *
     * @param employeeId 员工ID
     * @param companyId  公司ID
     * @return com.luohuo.basic.base.R<java.util.List < com.luohuo.flex.model.entity.base.SysOrg>>
     * @author tangyh
     * @date 2022/11/18 2:26 PM
     * @create [2022/11/18 2:26 PM ] [tangyh] [初始创建]
     */
    @GetMapping("/baseOrg/findDeptByEmployeeId")
    R<List<SysOrg>> findDeptByEmployeeId(@RequestParam("employeeId") Long employeeId, @RequestParam("companyId") Long companyId);

    /**
     * 查询员工的 公司
     *
     * @param employeeId 员工
     * @return com.luohuo.basic.base.R<java.util.List < com.luohuo.flex.model.entity.base.SysOrg>>
     * @author tangyh
     * @date 2022/11/18 2:26 PM
     * @create [2022/11/18 2:26 PM ] [tangyh] [初始创建]
     */
    @GetMapping("/baseOrg/findCompanyByEmployeeId")
    R<List<SysOrg>> findCompanyByEmployeeId(@RequestParam("employeeId") Long employeeId);
}
