package com.luohuo.flex.controller.user;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.luohuo.flex.base.vo.save.tenant.DefTenantAdminVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.luohuo.basic.annotation.log.WebLog;
import com.luohuo.basic.base.R;
import com.luohuo.basic.base.controller.SuperCacheController;
import com.luohuo.basic.base.request.PageParams;
import com.luohuo.basic.interfaces.echo.EchoService;
import com.luohuo.flex.base.biz.user.BaseEmployeeBiz;
import com.luohuo.flex.base.entity.user.BaseEmployee;
import com.luohuo.flex.base.service.user.BaseEmployeeService;
import com.luohuo.flex.base.vo.query.user.BaseEmployeePageQuery;
import com.luohuo.flex.base.vo.result.user.BaseEmployeeResultVO;
import com.luohuo.flex.base.vo.save.user.BaseEmployeeRoleRelSaveVO;
import com.luohuo.flex.base.vo.save.user.BaseEmployeeSaveVO;
import com.luohuo.flex.base.vo.update.user.BaseEmployeeUpdateVO;

import java.util.List;


/**
 * <p>
 * 前端控制器
 * 员工
 * </p>
 *
 * @author 乾乾
 * @date 2021-10-18
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/baseEmployee")
@Tag(name = "员工")
public class BaseEmployeeController extends SuperCacheController<BaseEmployeeService, Long, BaseEmployee, BaseEmployeeSaveVO, BaseEmployeeUpdateVO,
        BaseEmployeePageQuery, BaseEmployeeResultVO> {

    private final EchoService echoService;
    private final BaseEmployeeBiz baseEmployeeBiz;

    @Override
    public EchoService getEchoService() {
        return echoService;
    }

    @Override
    @WebLog(value = "'分页列表查询:第' + #params?.current + '页, 显示' + #params?.size + '行'", response = false)
    public R<IPage<BaseEmployeeResultVO>> page(@RequestBody @Validated PageParams<BaseEmployeePageQuery> params) {
        IPage<BaseEmployeeResultVO> page = baseEmployeeBiz.findPageResultVO(params);
        handlerResult(page);
        return R.success(page);
    }

    @Override
    @WebLog("'查询:' + #id")
    public R<BaseEmployeeResultVO> get(@PathVariable Long id) {
        return success(baseEmployeeBiz.getEmployeeUserById(id));
    }

    /**
     * 给员工分配角色
     *
     * @param employeeRoleSaveVO 参数
     * @return 新增结果
     */
    @Operation(summary = "给员工分配角色", description = "给员工分配角色")
    @PostMapping("/employeeRole")
    @WebLog("给员工分配角色")
    public R<List<Long>> saveEmployeeRole(@RequestBody BaseEmployeeRoleRelSaveVO employeeRoleSaveVO) {
        return success(superService.saveEmployeeRole(employeeRoleSaveVO));
    }

    /**
     * 查询员工的角色
     *
     * @param employeeId 员工id
     * @return 新增结果
     */
    @Operation(summary = "查询员工的角色")
    @GetMapping("/findEmployeeRoleByEmployeeId")
    @WebLog("查询员工的角色")
    public R<List<Long>> findEmployeeRoleByEmployeeId(@RequestParam Long employeeId) {
        return success(superService.findEmployeeRoleByEmployeeId(employeeId));
    }

    @Override
    public R<BaseEmployee> handlerSave(BaseEmployeeSaveVO model) {
        return R.success(baseEmployeeBiz.save(model));
    }

	// ========================== 以下为租户控制模块 ==========================
	@Operation(summary = "邀请进企业并设置为租户管理员")
	@PostMapping(value = "/tenantAdmin")
	@WebLog("设置为租户管理员")
	public R<Boolean> tenantAdmin(@RequestBody @Validated DefTenantAdminVO param) {
		return R.success(param.getIsBind() != null && param.getIsBind() ? baseEmployeeBiz.bindUser(param) : baseEmployeeBiz.unBindUser(param));
	}

	@Operation(summary = "租户管理员邀请用户进入企业")
	@PostMapping(value = "/invitationUser")
	@WebLog("租户管理员邀请用户进入企业")
	public R<Boolean> invitationUser(@RequestBody @Validated DefTenantAdminVO param) {
		return R.success(param.getIsBind() != null && param.getIsBind() ? baseEmployeeBiz.invitationUser(param) : baseEmployeeBiz.unInvitationUser(param));
	}

	// TODO 还少一个添加RAM子账号的功能
}
