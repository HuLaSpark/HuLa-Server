package com.luohuo.flex.controller.tenant;

import com.luohuo.basic.annotation.log.WebLog;
import com.luohuo.basic.base.R;
import com.luohuo.basic.base.controller.SuperCacheController;
import com.luohuo.basic.base.request.PageParams;
import com.luohuo.basic.context.ContextUtil;
import com.luohuo.basic.database.mybatis.conditions.query.QueryWrap;
import com.luohuo.basic.interfaces.echo.EchoService;
import com.luohuo.flex.base.entity.tenant.DefTenant;
import com.luohuo.flex.base.service.tenant.DefTenantService;
import com.luohuo.flex.base.vo.query.tenant.DefTenantPageQuery;
import com.luohuo.flex.base.vo.result.user.DefTenantResultVO;
import com.luohuo.flex.base.vo.save.tenant.DefTenantSaveVO;
import com.luohuo.flex.base.vo.update.tenant.DefTenantUpdateVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 租户管理
 * </p>
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/anyone/defTenant")
@Tag(name = "租户管理")
public class DefTenantAnyoneController extends SuperCacheController<DefTenantService, Long, DefTenant, DefTenantSaveVO, DefTenantUpdateVO, DefTenantPageQuery, DefTenantResultVO> {

    private final EchoService echoService;

    @Override
    public EchoService getEchoService() {
        return echoService;
    }

    @Operation(summary = "修改租户审核状态", description = "修改租户审核状态")
    @PostMapping("/updateStatus")
    @WebLog("修改租户审核状态")
    public R<Boolean> updateStatus(@NotNull(message = "请修改正确的企业") @RequestParam Long id, @RequestParam @NotNull(message = "请传递状态值") Integer status, @RequestParam(required = false) String reviewComments) {
        return success(superService.updateStatus(id, status, reviewComments));
    }

    @Override
    public QueryWrap<DefTenant> handlerWrapper(DefTenant model, PageParams<DefTenantPageQuery> params) {
        QueryWrap<DefTenant> wrap = super.handlerWrapper(model, params);
        wrap.lambda().eq(DefTenant::getCreateBy, ContextUtil.getUid());
        return wrap;
    }

    @Override
    public R<DefTenant> save(@RequestBody @Validated DefTenantSaveVO defTenantSaveVO) {
        return R.success(superService.register(defTenantSaveVO));
    }
}
