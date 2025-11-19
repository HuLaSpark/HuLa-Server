package com.luohuo.flex.test.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.luohuo.basic.base.controller.SuperController;
import com.luohuo.basic.interfaces.echo.EchoService;
import com.luohuo.flex.test.entity.DefGenTestSimple;
import com.luohuo.flex.test.service.DefGenTestSimpleService;
import com.luohuo.flex.test.vo.query.DefGenTestSimplePageQuery;
import com.luohuo.flex.test.vo.result.DefGenTestSimpleResultVO;
import com.luohuo.flex.test.vo.save.DefGenTestSimpleSaveVO;
import com.luohuo.flex.test.vo.update.DefGenTestSimpleUpdateVO;

/**
 * <p>
 * 前端控制器
 * 测试单表
 * </p>
 *
 * @author 乾乾
 * @date 2022-04-15 15:36:45
 * @create [2022-04-15 15:36:45] [zuihou] [代码生成器生成]
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/defGenTestSimple")
@Tag(name = "测试单表")
public class DefGenTestSimpleController extends SuperController<DefGenTestSimpleService, Long, DefGenTestSimple, DefGenTestSimpleSaveVO,
        DefGenTestSimpleUpdateVO, DefGenTestSimplePageQuery, DefGenTestSimpleResultVO> {
    private final EchoService echoService;

    @Override
    public EchoService getEchoService() {
        return echoService;
    }

}


