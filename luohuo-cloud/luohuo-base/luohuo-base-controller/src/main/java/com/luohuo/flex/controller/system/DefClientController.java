package com.luohuo.flex.controller.system;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.luohuo.basic.base.controller.SuperController;
import com.luohuo.basic.interfaces.echo.EchoService;
import com.luohuo.flex.base.entity.system.DefClient;
import com.luohuo.flex.base.service.system.DefClientService;
import com.luohuo.flex.base.vo.save.system.DefClientSaveVO;
import com.luohuo.flex.base.vo.query.system.DefClientPageQuery;
import com.luohuo.flex.base.vo.result.system.DefClientResultVO;
import com.luohuo.flex.base.vo.update.system.DefClientUpdateVO;


/**
 * <p>
 * 前端控制器
 * 客户端
 * </p>
 *
 * @author 乾乾
 * @date 2021-10-13
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/defClient")
@Tag(name = "客户端")
public class DefClientController extends SuperController<DefClientService, Long, DefClient, DefClientSaveVO, DefClientUpdateVO, DefClientPageQuery, DefClientResultVO> {

    private final EchoService echoService;

    @Override
    public EchoService getEchoService() {
        return echoService;
    }

}
