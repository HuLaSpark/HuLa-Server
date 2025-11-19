package com.luohuo.flex.base.biz.user;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.ImmutableMap;
import com.luohuo.basic.context.ContextUtil;
import com.luohuo.basic.database.mybatis.conditions.Wraps;
import com.luohuo.basic.utils.ArgumentAssert;
import com.luohuo.flex.base.entity.tenant.DefUserTenantRel;
import com.luohuo.flex.base.service.tenant.DefUserTenantRelService;
import com.luohuo.flex.base.vo.save.tenant.DefTenantAdminVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.luohuo.basic.base.request.PageParams;
import com.luohuo.basic.exception.BizException;
import com.luohuo.basic.utils.CollHelper;
import com.luohuo.flex.base.entity.tenant.DefUser;
import com.luohuo.flex.base.entity.user.BaseEmployee;
import com.luohuo.flex.base.service.tenant.DefUserService;
import com.luohuo.flex.base.service.user.BaseEmployeeOrgRelService;
import com.luohuo.flex.base.service.user.BaseEmployeeService;
import com.luohuo.flex.base.vo.query.tenant.DefUserPageQuery;
import com.luohuo.flex.base.vo.query.user.BaseEmployeePageQuery;
import com.luohuo.flex.base.vo.result.user.BaseEmployeeResultVO;
import com.luohuo.flex.base.vo.save.tenant.DefUserSaveVO;
import com.luohuo.flex.base.vo.save.user.BaseEmployeeSaveVO;
import com.luohuo.flex.model.enumeration.base.ActiveStatusEnum;

import java.util.List;

/**
 * 员工大业务层
 *
 * @author 乾乾
 * @date 2021/10/22 10:37
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BaseEmployeeBiz {
    private final BaseEmployeeService baseEmployeeService;
    private final BaseEmployeeOrgRelService baseEmployeeOrgRelService;
	private final DefUserTenantRelService defUserTenantRelService;
    private final DefUserService defUserService;

    /**
     * 保存员工信息
     *
     * @param saveVO saveVO
     * @return com.luohuo.flex.base.entity.user.BaseEmployee
     * @author tangyh
     * @date 2022/10/28 12:15 AM
     * @create [2022/10/28 12:15 AM ] [tangyh] [初始创建]
     */
    @Transactional(rollbackFor = Exception.class)
    public BaseEmployee save(BaseEmployeeSaveVO saveVO) {
        boolean existDefUser = defUserService.checkMobile(saveVO.getMobile(), null);
        if (existDefUser) {
            throw new BizException("手机号已被注册,请重新输入手机号 或 直接邀请它加入贵公司。");
        }
        String username = StrUtil.isBlank(saveVO.getUsername()) ? IdUtil.simpleUUID() : saveVO.getUsername();
        // 保存 用户表 和 员工表
        DefUserSaveVO userSaveVO = BeanUtil.toBean(saveVO, DefUserSaveVO.class);
        userSaveVO.setUsername(username);
        userSaveVO.setNickName(saveVO.getRealName());
        DefUser defUser = defUserService.save(userSaveVO);

        // 保存员工表
        saveVO.setActiveStatus(ActiveStatusEnum.ACTIVATED.getCode());
        saveVO.setUserId(defUser.getId());
        saveVO.setIsDefault(true);
        return baseEmployeeService.save(saveVO);
    }

    /**
     * 根据员工ID 查询员工、用户和他所在的机构 信息
     *
     * @param employeeId 员工ID
     * @return com.luohuo.flex.base.vo.result.user.BaseEmployeeResultVO
     * @author tangyh
     * @date 2022/10/28 12:13 AM
     * @create [2022/10/28 12:13 AM ] [tangyh] [初始创建]
     */
    public BaseEmployeeResultVO getEmployeeUserById(Long employeeId) {
        // 租户库
        BaseEmployee employee = baseEmployeeService.getById(employeeId);
        if (employee == null) {
            return null;
        }
        // 员工信息
        BaseEmployeeResultVO resultVO = new BaseEmployeeResultVO();
        BeanUtil.copyProperties(employee, resultVO);

        // 机构信息
        resultVO.setOrgIdList(baseEmployeeOrgRelService.findOrgIdListByEmployeeId(employeeId));
        return resultVO;
    }

    /**
     * 分页查员工数据
     *
     * @param params 参数
     * @return IPage
     * @author tangyh
     * @date 2022/10/28 12:19 AM
     * @create [2022/10/28 12:19 AM ] [tangyh] [初始创建]
     */
    public IPage<BaseEmployeeResultVO> findPageResultVO(PageParams<BaseEmployeePageQuery> params) {
        BaseEmployeePageQuery pageQuery = params.getModel();
        List<Long> userIdList;
        if (!StrUtil.isAllEmpty(pageQuery.getMobile(), pageQuery.getEmail(), pageQuery.getUsername(), pageQuery.getIdCard())) {
            userIdList = defUserService.findUserIdList(BeanUtil.toBean(pageQuery, DefUserPageQuery.class));
            if (CollUtil.isEmpty(userIdList)) {
                return new Page<>(params.getCurrent(), params.getSize());
            }

            params.getModel().setUserIdList(userIdList);
        }
		return baseEmployeeService.findPageResultVO(params);
    }

	private List<BaseEmployee> findEmployeeList(DefTenantAdminVO param) {
		Long tenantId = param.getTenantId();
		ArgumentAssert.notNull(tenantId, "请选择租户");


		List<DefUser> defUsers = defUserService.listByIds(param.getUserIdList());
		ArgumentAssert.notEmpty(defUsers, "请选择用户");
		long employeeCount = defUserTenantRelService.count(Wraps.<DefUserTenantRel>lbQ().eq(DefUserTenantRel::getTenantId, tenantId).in(DefUserTenantRel::getUserId, param.getUserIdList()));
		ArgumentAssert.isFalse(employeeCount > 0, "对不起，您选择的用户已经是该企业的员工");

		// 保存def库的员工
		List<DefUserTenantRel> employeeList = param.getUserIdList().stream().map(userId -> {
			DefUserTenantRel employee = new DefUserTenantRel();
			employee.setUserId(userId);
			employee.setTenantId(tenantId);
			employee.setState(true);
			employee.setIsDefault(false);
			return employee;
		}).toList();
		defUserTenantRelService.saveBatch(employeeList);

		ImmutableMap<Long, String> userMap = CollHelper.uniqueIndex(defUsers, DefUser::getId, DefUser::getNickName);
		List<BaseEmployee> baseEmployeeList = BeanUtil.copyToList(employeeList, BaseEmployee.class);
		baseEmployeeList.forEach(employee -> {
			employee.setActiveStatus(ActiveStatusEnum.ACTIVATED.getCode());
			employee.setState(true);
		});

		// 保存到指定租户的 base库的员工 + 租户管理员角色
//		ContextUtil.setTenantBasePoolName(tenantId);
		return baseEmployeeList;
	}

	/**
	 * TODO 这几个接口后续改为消息状态表实现事务
	 * 将用户绑定某个租户的员工
	 *
	 * @param param param
	 */
//	@GlobalTransactional
	public Boolean bindUser(DefTenantAdminVO param) {
		List<BaseEmployee> baseEmployeeList = findEmployeeList(param);
		return baseEmployeeService.saveBatchBaseEmployeeAndRole(baseEmployeeList);
	}

	private List<Long> findEmployeeIdList(DefTenantAdminVO param, Long tenantId) {
		List<DefUser> defUsers = defUserService.listByIds(param.getUserIdList());
		ArgumentAssert.notEmpty(defUsers, "请选择用户");
		List<DefUserTenantRel> defEmployeeList = defUserTenantRelService.list(Wraps.<DefUserTenantRel>lbQ().eq(DefUserTenantRel::getTenantId, tenantId).in(DefUserTenantRel::getUserId, param.getUserIdList()));
		ArgumentAssert.notEmpty(defEmployeeList, "对不起，您选择的用户不是该租户的员工");
		List<Long> employeeIdList = defEmployeeList.stream().map(DefUserTenantRel::getId).toList();
		defUserTenantRelService.removeByIds(employeeIdList);
		return employeeIdList;
	}

	/**
	 * 从租户将用户解绑
	 */
//	@GlobalTransactional
	public Boolean unBindUser(DefTenantAdminVO param) {
		Long tenantId = param.getTenantId();
		ArgumentAssert.notNull(tenantId, "请选择租户");

		List<Long> employeeIdList = findEmployeeIdList(param, tenantId);

//		ContextUtil.setTenantBasePoolName(tenantId);
		return baseEmployeeService.removeByIds(employeeIdList);
	}

	/**
	 * 邀请某个用户进入租户
	 */
//	@GlobalTransactional
	public Boolean invitationUser(DefTenantAdminVO param) {
		param.setTenantId(ContextUtil.getTenantId());
		List<BaseEmployee> baseEmployeeList = findEmployeeList(param);
		return baseEmployeeService.saveBatch(baseEmployeeList);
	}

	/**
	 * 从租户将用户移除
	 */
//	@GlobalTransactional
	public Boolean unInvitationUser(DefTenantAdminVO param) {
		Long tenantId = ContextUtil.getTenantId();
		List<Long> employeeIdList = findEmployeeIdList(param, tenantId);

		return baseEmployeeService.removeByIds(employeeIdList);
	}

}
