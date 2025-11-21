package com.luohuo.flex.msg.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.luohuo.basic.base.controller.SuperController;
import com.luohuo.basic.interfaces.echo.EchoService;
import com.luohuo.flex.msg.entity.ExtendInterfaceLogging;
import com.luohuo.flex.msg.service.ExtendInterfaceLoggingService;
import com.luohuo.flex.msg.vo.query.ExtendInterfaceLoggingPageQuery;
import com.luohuo.flex.msg.vo.result.ExtendInterfaceLoggingResultVO;
import com.luohuo.flex.msg.vo.save.ExtendInterfaceLoggingSaveVO;
import com.luohuo.flex.msg.vo.update.ExtendInterfaceLoggingUpdateVO;

/**
 * <p>
 * 前端控制器
 * 接口执行日志记录
 * </p>
 *
 * @author 乾乾
 * @date 2022-07-09 23:58:59
 * @create [2022-07-09 23:58:59] [zuihou] [代码生成器生成]
 */
@Slf4j
@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/extendInterfaceLogging")
@Tag(name = "接口执行日志记录")
public class ExtendInterfaceLoggingController extends SuperController<ExtendInterfaceLoggingService, Long, ExtendInterfaceLogging, ExtendInterfaceLoggingSaveVO,
        ExtendInterfaceLoggingUpdateVO, ExtendInterfaceLoggingPageQuery, ExtendInterfaceLoggingResultVO> {
    private final EchoService echoService;

    @Override
    public EchoService getEchoService() {
        return echoService;
    }

}


