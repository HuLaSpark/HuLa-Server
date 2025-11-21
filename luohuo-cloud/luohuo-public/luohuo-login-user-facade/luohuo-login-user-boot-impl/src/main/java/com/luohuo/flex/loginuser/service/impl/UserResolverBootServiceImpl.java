package com.luohuo.flex.loginuser.service.impl;

import cn.hutool.core.bean.BeanUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import com.luohuo.basic.base.R;
import com.luohuo.flex.base.entity.tenant.DefUser;
import com.luohuo.flex.base.entity.user.BaseEmployee;
import com.luohuo.flex.base.entity.user.BaseOrg;
import com.luohuo.flex.base.entity.user.BasePosition;
import com.luohuo.flex.base.service.system.BaseRoleService;
import com.luohuo.flex.base.service.tenant.DefUserService;
import com.luohuo.flex.base.service.user.BaseEmployeeService;
import com.luohuo.flex.base.service.user.BaseOrgService;
import com.luohuo.flex.base.service.user.BasePositionService;
import com.luohuo.flex.model.entity.base.SysEmployee;
import com.luohuo.flex.model.entity.base.SysOrg;
import com.luohuo.flex.model.entity.base.SysPosition;
import com.luohuo.flex.model.entity.system.SysUser;
import com.luohuo.flex.model.vo.result.UserQuery;
import com.luohuo.flex.oauth.biz.ResourceBiz;
import com.luohuo.flex.userinfo.service.UserResolverService;

import java.util.List;

/**
 * 用户注入 boot 版本实现
 *
 * @author 乾乾
 * @since 2024年09月20日14:15:47
 *
 */
@Component
@RequiredArgsConstructor
public class UserResolverBootServiceImpl implements UserResolverService {
    private final DefUserService defUserService;
    private final BaseEmployeeService baseEmployeeService;
    private final BaseRoleService baseRoleService;
    private final BaseOrgService baseOrgService;
    private final BasePositionService basePositionService;
    private final ResourceBiz resourceBiz;

    private boolean notEmpty(Long val) {
        return val != null && !Long.valueOf(0).equals(val);
    }

    @Override
    public R<SysUser> getById(UserQuery query) {
        Long userId = query.getUserId();
        DefUser defUser = defUserService.getByIdCache(userId);
        if (defUser == null) {
            return R.success(new SysUser());
        }
        SysUser sysUser = BeanUtil.toBean(defUser, SysUser.class);
        boolean notEmptyEmployee = notEmpty(query.getEmployeeId());
        boolean queryEmployee = query.getFull() || query.getEmployee();
        boolean queryOrg = query.getFull() || query.getOrg();
        boolean queryCurrentOrg = query.getFull() || query.getCurrentOrg();
        boolean queryPosition = query.getFull() || query.getPosition();
        boolean queryResource = query.getFull() || query.getResource();
        boolean queryRoles = query.getFull() || query.getRoles();
        boolean anyQuery = queryEmployee || queryOrg || queryCurrentOrg || queryPosition || queryResource || queryRoles;
        if (notEmptyEmployee && anyQuery) {
            BaseEmployee baseEmployee = baseEmployeeService.getByIdCache(query.getEmployeeId());
            if (baseEmployee == null) {
                return R.success(sysUser);
            }
            sysUser.setEmployee(BeanUtil.toBean(baseEmployee, SysEmployee.class));
            // 当前单位
            if (queryCurrentOrg && notEmpty(baseEmployee.getLastCompanyId())) {
                BaseOrg baseOrg = baseOrgService.getByIdCache(baseEmployee.getLastCompanyId());
                sysUser.setCompany(BeanUtil.toBean(baseOrg, SysOrg.class));
            }
            // 当前部门
            if (queryCurrentOrg && notEmpty(baseEmployee.getLastDeptId())) {
                BaseOrg baseOrg = baseOrgService.getByIdCache(baseEmployee.getLastDeptId());
                sysUser.setDept(BeanUtil.toBean(baseOrg, SysOrg.class));
            }
            // 他所在的 单位和部门
            if (queryOrg) {
                List<BaseOrg> companyList = baseOrgService.findCompanyByEmployeeId(baseEmployee.getId());
                sysUser.setCompanyList(BeanUtil.copyToList(companyList, SysOrg.class));

                List<BaseOrg> deptList = baseOrgService.findDeptByEmployeeId(baseEmployee.getId(), baseEmployee.getLastCompanyId());
                sysUser.setDeptList(BeanUtil.copyToList(deptList, SysOrg.class));
            }
            // 岗位
            if (queryPosition && notEmpty(baseEmployee.getPositionId())) {
                BasePosition basePosition = basePositionService.getById(baseEmployee.getPositionId());
                sysUser.setPosition(BeanUtil.toBean(basePosition, SysPosition.class));
            }
            // 资源
            if (queryResource) {
                List<String> resources = resourceBiz.findVisibleResource(baseEmployee.getId(), null);
                sysUser.setResourceCodeList(resources);
            }
            // 角色
            if (queryRoles) {
                List<String> codes = baseRoleService.findRoleCodeByEmployeeId(baseEmployee.getId());
                sysUser.setRoleCodeList(codes);
            }
        }
        return R.success(sysUser);
    }
}
