package com.luohuo.flex.msg.api;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.luohuo.basic.base.R;
import com.luohuo.basic.constant.Constants;
import com.luohuo.flex.msg.vo.update.ExtendMsgSendVO;

/**
 * 文件接口
 *
 * @author 乾乾
 * @date 2019/06/21
 */
@FeignClient(name = "${" + Constants.PROJECT_PREFIX + ".feign.base-server:luohuo-base-server}")
public interface MsgApi {

    /**
     * 根据模板发送消息
     *
     * @param data 发送内容
     * @return
     */
    @Operation(summary = "根据模板发送消息", description = "根据模板发送消息")
    @PostMapping("/extendMsg/anyUser/sendByTemplate")
    R<Boolean> sendByTemplate(@RequestBody ExtendMsgSendVO data);
}
