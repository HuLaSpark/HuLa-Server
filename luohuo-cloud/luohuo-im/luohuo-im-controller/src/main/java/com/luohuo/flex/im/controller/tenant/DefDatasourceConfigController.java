package com.luohuo.flex.im.controller.tenant;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.luohuo.basic.base.R;
import com.luohuo.basic.base.controller.SuperController;
import com.luohuo.basic.interfaces.echo.EchoService;
import com.luohuo.flex.im.entity.tenant.DefDatasourceConfig;
import com.luohuo.flex.im.service.tenant.DefDatasourceConfigService;
import com.luohuo.flex.im.vo.query.tenant.DefDatasourceConfigPageQuery;
import com.luohuo.flex.im.vo.result.tenant.DefDatasourceConfigResultVO;
import com.luohuo.flex.im.vo.save.tenant.DefDatasourceConfigSaveVO;
import com.luohuo.flex.im.vo.update.tenant.DefDatasourceConfigUpdateVO;


/**
 * <p>
 * 前端控制器
 * 数据源
 * </p>
 *
 * @author 乾乾
 * @date 2021-09-13
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/defDatasourceConfig")
@Tag(name = "数据源")
public class DefDatasourceConfigController extends SuperController<DefDatasourceConfigService, Long, DefDatasourceConfig, DefDatasourceConfigSaveVO, DefDatasourceConfigUpdateVO, DefDatasourceConfigPageQuery, DefDatasourceConfigResultVO> {

    private final EchoService echoService;

    @Override
    public EchoService getEchoService() {
        return echoService;
    }


    @Operation(summary = "测试数据库链接")
    @PostMapping("/testConnect")
    public R<Boolean> testConnect(@RequestParam Long id) {
        return R.success(superService.testConnection(id));
    }
}
