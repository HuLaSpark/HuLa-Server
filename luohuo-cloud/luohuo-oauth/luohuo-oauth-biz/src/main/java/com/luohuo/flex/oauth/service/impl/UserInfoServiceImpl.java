package com.luohuo.flex.oauth.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.luohuo.basic.exception.BizException;
import com.luohuo.flex.base.vo.save.user.BaseEmployeeOrgRelSaveVO;
import com.luohuo.flex.base.vo.save.user.BaseEmployeeRoleRelSaveVO;
import com.luohuo.flex.im.api.ImUserApi;
import com.luohuo.flex.im.api.vo.UserRegisterVo;
import com.luohuo.flex.im.enums.UserTypeEnum;
import com.luohuo.flex.model.entity.system.SysUser;
import com.luohuo.flex.oauth.emuns.LoginEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.luohuo.basic.cache.redis2.CacheResult;
import com.luohuo.basic.cache.repository.CacheOps;
import com.luohuo.basic.context.ContextUtil;
import com.luohuo.basic.model.cache.CacheKey;
import com.luohuo.basic.utils.ArgumentAssert;
import com.luohuo.flex.base.entity.tenant.DefUser;
import com.luohuo.flex.base.entity.user.BaseEmployee;
import com.luohuo.flex.base.entity.user.BaseOrg;
import com.luohuo.flex.base.service.tenant.DefUserService;
import com.luohuo.flex.base.service.user.BaseEmployeeService;
import com.luohuo.flex.base.service.user.BaseOrgService;
import com.luohuo.flex.common.cache.common.CaptchaCacheKeyBuilder;
import com.luohuo.flex.common.properties.SystemProperties;
import com.luohuo.flex.oauth.service.UserInfoService;
import com.luohuo.flex.oauth.vo.param.RegisterByEmailVO;
import com.luohuo.flex.oauth.vo.param.RegisterByMobileVO;
import com.luohuo.flex.oauth.vo.result.OrgResultVO;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * @author tangyh
 * @version v1.0
 * @date 2022/9/16 12:21 PM
 * @create [2022/9/16 12:21 PM ] [tangyh] [初始创建]
 */
@Service
@RequiredArgsConstructor
public class UserInfoServiceImpl implements UserInfoService {
    protected final BaseEmployeeService baseEmployeeService;
    protected final BaseOrgService baseOrgService;
    protected final DefUserService defUserService;
    protected final CacheOps cacheOps;
    protected final SystemProperties systemProperties;
    private final ImUserApi imUserApi;

    @Override
    public OrgResultVO findCompanyAndDept() {
        Long userId = ContextUtil.getUserId();
        Long companyId = ContextUtil.getCurrentCompanyId();
        Long deptId = ContextUtil.getCurrentDeptId();
        BaseEmployee baseEmployee = baseEmployeeService.getEmployeeByUser(userId);
        ArgumentAssert.notNull(baseEmployee, "用户不属于该企业");

        // 上次登录的单位
        List<BaseOrg> orgList = baseOrgService.findOrgByEmployeeId(baseEmployee.getId());

        Long currentCompanyId = companyId != null ? companyId : baseEmployee.getLastCompanyId();

        Long currentDeptId = deptId != null ? deptId : baseEmployee.getLastDeptId();
        return OrgResultVO.builder()
                .orgList(orgList)
                .employeeId(baseEmployee.getId())
                .currentCompanyId(currentCompanyId)
                .currentDeptId(currentDeptId).build();
    }

    @Override
    public List<BaseOrg> findDeptByCompany(Long companyId, Long employeeId) {
        return baseOrgService.findDeptByEmployeeId(employeeId, companyId);
    }

    @Override
    public String registerByMobile(RegisterByMobileVO register) {
        if (systemProperties.getVerifyCaptcha()) {
//            短信验证码
            CacheKey cacheKey = new CaptchaCacheKeyBuilder().key(register.getMobile(), register.getKey());
            CacheResult<String> code = cacheOps.get(cacheKey);
            ArgumentAssert.equals(code.getValue(), register.getCode(), "验证码不正确");
        }
        ArgumentAssert.equals(register.getConfirmPassword(), register.getPassword(), "密码和确认密码不一致");
        DefUser defUser = BeanUtil.toBean(register, DefUser.class);

        defUserService.register(defUser);
        return defUser.getMobile();
    }

    @Override
    @Transactional
    public String registerByEmail(SysUser sysUser, RegisterByEmailVO register) {
		// 1. 校验数据保存defUser
        if (systemProperties.getVerifyCaptcha()) {
            CacheKey cacheKey = new CaptchaCacheKeyBuilder().key(register.getEmail(), register.getKey());
            CacheResult<String> code = cacheOps.get(cacheKey);
            ArgumentAssert.equals(code.getValue(), register.getCode(), "验证码不正确");
        }
        ArgumentAssert.equals(register.getConfirmPassword(), register.getPassword(), "密码和确认密码不一致");
        DefUser defUser = BeanUtil.toBean(register, DefUser.class);
		try {
			defUserService.registerByEmail(defUser);
		} catch (Exception e) {
			if(e.getMessage().contains("Duplicate")){
				throw new BizException("账号已存在！");
			}
		}

		// 2. 根据注册系统构造子系统需要的user参数
		return switch (LoginEnum.get(register.getSystemType())) {
			case IM -> {
				UserRegisterVo userRegisterVo = new UserRegisterVo();
				userRegisterVo.setAccount(defUser.getUsername());
				userRegisterVo.setEmail(register.getEmail());
				userRegisterVo.setUserId(defUser.getId());
				userRegisterVo.setName(defUser.getNickName());
				userRegisterVo.setSex(defUser.getSex());
				userRegisterVo.setAvatar(defUser.getAvatar());
				userRegisterVo.setTenantId(defUser.getTenantId());
				userRegisterVo.setUserType(UserTypeEnum.NORMAL.getValue());
				if(!imUserApi.register(userRegisterVo).getData()){
					throw new BizException("该邮箱已被其他账号绑定");
				}
				yield defUser.getEmail();
			}
			case MANAGER -> {
				// 2.1 注册后台管理员、RAM账号
				BaseEmployee baseEmployee = new BaseEmployee();
				baseEmployee.setName(defUser.getUsername());
				baseEmployee.setUserId(defUser.getId());
				// 用户注册时，默认主账号【不赋予特殊权限即可】
				baseEmployee.setUserType(1);
				baseEmployee.setParentId(0L);
				baseEmployee.setActiveStatus("20");
				baseEmployee.setPositionStatus("10");
				baseEmployee.setState(true);
				baseEmployee.setTenantId(defUser.getTenantId());
				BaseEmployee employee = baseEmployeeService.save(baseEmployee);

				// 2.2 关联默认组织
				BaseEmployeeOrgRelSaveVO employeeOrgRel = new BaseEmployeeOrgRelSaveVO();
				employeeOrgRel.setEmployeeId(employee.getId());
				employeeOrgRel.setOrgId(1L);	// 给一个默认的组织结构
				baseOrgService.saveEmployeeOrg(employeeOrgRel);

				// 2.3 关联默认角色
				BaseEmployeeRoleRelSaveVO employeeRoleRel = new BaseEmployeeRoleRelSaveVO();
				employeeRoleRel.setEmployeeId(employee.getId());
				employeeRoleRel.setRoleIdList(Arrays.asList(1460615729169563648L));	// 给一个默认的角色
				baseEmployeeService.saveEmployeeRole(employeeRoleRel);

				// 2.4 关联默认租户


				yield defUser.getEmail();
			}
			default ->  {
				yield defUser.getEmail();
			}
		};
    }

	@Override
	public Boolean checkEmail(String email) {
		// 1. 判断系统邮箱是否存在
		boolean systemEmail = defUserService.checkEmail(email, null);

		// 2. 判断Im邮箱是否存在
		boolean imEmail = imUserApi.checkEmail(email).getData();
		return systemEmail || imEmail;
	}
}
