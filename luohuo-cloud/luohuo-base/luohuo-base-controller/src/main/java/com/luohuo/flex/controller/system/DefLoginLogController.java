package com.luohuo.flex.controller.system;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.luohuo.basic.annotation.log.WebLog;
import com.luohuo.basic.base.R;
import com.luohuo.basic.base.controller.SuperController;
import com.luohuo.basic.interfaces.echo.EchoService;
import com.luohuo.flex.base.entity.system.DefLoginLog;
import com.luohuo.flex.base.service.system.DefLoginLogService;
import com.luohuo.flex.base.vo.result.system.DefLoginLogResultVO;
import com.luohuo.flex.base.vo.save.system.DefLoginLogSaveVO;
import com.luohuo.flex.base.vo.update.system.DefLoginLogUpdateVO;
import com.luohuo.flex.base.vo.query.system.DefLoginLogPageQuery;
import com.luohuo.basic.base.request.PageParams;

import java.time.LocalDateTime;


/**
 * <p>
 * 前端控制器
 * 登录日志
 * </p>
 *
 * @author 乾乾
 * @date 2021-11-12
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/defLoginLog")
@Tag(name = "登录日志")
public class DefLoginLogController extends SuperController<DefLoginLogService, Long, DefLoginLog, DefLoginLogSaveVO,
        DefLoginLogUpdateVO, DefLoginLogPageQuery, DefLoginLogResultVO> {

    private final EchoService echoService;

    @Override
    public EchoService getEchoService() {
        return echoService;
    }


    @Operation(summary = "清空日志")
    @DeleteMapping("clear")
    @WebLog("清空日志")
    public R<Boolean> clear(@RequestParam(required = false, defaultValue = "1") Integer type) {
        LocalDateTime clearBeforeTime = null;
        Integer clearBeforeNum = null;
        if (type == 1) {
            clearBeforeTime = LocalDateTime.now().plusMonths(-1);
        } else if (type == 2) {
            clearBeforeTime = LocalDateTime.now().plusMonths(-3);
        } else if (type == 3) {
            clearBeforeTime = LocalDateTime.now().plusMonths(-6);
        } else if (type == 4) {
            clearBeforeTime = LocalDateTime.now().plusMonths(-12);
        } else if (type == 5) {
            // 清理一千条以前日志数据
            clearBeforeNum = 1000;
        } else if (type == 6) {
            // 清理一万条以前日志数据
            clearBeforeNum = 10000;
        } else if (type == 7) {
            // 清理三万条以前日志数据
            clearBeforeNum = 30000;
        } else if (type == 8) {
            // 清理十万条以前日志数据
            clearBeforeNum = 100000;
        }
        return success(superService.clearLog(clearBeforeTime, clearBeforeNum));
    }

    @Override
    public void handlerQueryParams(PageParams<DefLoginLogPageQuery> params) {
		params.setSort("createTime");
		params.setOrder("descending");
    }
}
