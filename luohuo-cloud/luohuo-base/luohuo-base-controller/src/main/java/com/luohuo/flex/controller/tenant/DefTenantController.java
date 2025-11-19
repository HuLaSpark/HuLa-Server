package com.luohuo.flex.controller.tenant;

import com.luohuo.basic.annotation.log.WebLog;
import com.luohuo.basic.base.R;
import com.luohuo.basic.base.controller.SuperCacheController;
import com.luohuo.basic.context.ContextUtil;
import com.luohuo.basic.database.mybatis.conditions.Wraps;
import com.luohuo.basic.interfaces.echo.EchoService;
import com.luohuo.flex.base.entity.tenant.DefTenant;
import com.luohuo.flex.base.service.tenant.DefTenantService;
import com.luohuo.flex.base.vo.query.tenant.DefTenantPageQuery;
import com.luohuo.flex.base.vo.result.user.DefTenantResultVO;
import com.luohuo.flex.base.vo.save.tenant.DefTenantSaveVO;
import com.luohuo.flex.base.vo.update.tenant.DefTenantUpdateVO;
import com.luohuo.flex.model.enumeration.system.DefTenantStatusEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * <p>
 * 前端控制器
 * 租户； TODO 租户独自数据源还未实现， 3.0 rust版本再来实现
 * </p>
 *
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/defTenant")
@Tag(name = "企业")
public class DefTenantController extends SuperCacheController<DefTenantService, Long, DefTenant, DefTenantSaveVO, DefTenantUpdateVO, DefTenantPageQuery, DefTenantResultVO> {

    private final EchoService echoService;

    @Override
    public EchoService getEchoService() {
        return echoService;
    }

    @Operation(summary = "查询所有企业", description = "查询所有企业")
    @GetMapping("/all")
    @WebLog("查询所有企业")
    public R<List<DefTenant>> list() {
        return success(superService.list(Wraps.<DefTenant>lbQ().eq(DefTenant::getStatus, DefTenantStatusEnum.NORMAL)));
    }

    @Operation(summary = "检测租户是否存在", description = "检测租户是否存在")
    @GetMapping("/check/{code}")
    @WebLog("检测租户是否存在")
    public R<Boolean> check(@PathVariable("code") String code) {
        return success(superService.check(code));
    }

    @Override
    public R<Boolean> handlerDelete(List<Long> ids) {
        return success(superService.delete(ids));
    }

    @Operation(summary = "删除租户和基础租户数据")
    @DeleteMapping("/deleteAll")
    @WebLog("删除租户和基础租户数据")
    public R<Boolean> deleteAll(@RequestBody List<Long> ids) {
        return success(superService.deleteAll(ids));
    }

    @Operation(summary = "修改租户审核状态", description = "修改租户审核状态")
    @PostMapping("/updateStatus")
    @WebLog("修改租户审核状态")
    public R<Boolean> updateStatus(@NotNull(message = "请修改正确的企业") @RequestParam Long id, @RequestParam @NotNull(message = "请传递状态值") Integer status, @RequestParam(required = false) String reviewComments) {
        return success(superService.updateStatus(id, status, reviewComments));
    }

    @Operation(summary = "修改租户状态", description = "修改租户状态")
    @PostMapping("/updateState")
    @WebLog("修改租户状态")
    public R<Boolean> updateState(@NotNull(message = "请修改正确的企业") @RequestParam Long id, @RequestParam @NotNull(message = "请传递状态值") Boolean state) {
        return success(superService.updateState(id, state));
    }

    @Operation(summary = "查询用户的可用企业", description = "查询用户的可用企业")
    @GetMapping("/listTenantByUserId")
    @WebLog("查询用户的可用企业")
    public R<List<DefTenantResultVO>> listTenantByUserId(@RequestParam(required = false) Long userId) {
        if (userId == null) {
            userId = ContextUtil.getUserId();
        }
        return success(superService.listTenantByUserId(userId));
    }
}
