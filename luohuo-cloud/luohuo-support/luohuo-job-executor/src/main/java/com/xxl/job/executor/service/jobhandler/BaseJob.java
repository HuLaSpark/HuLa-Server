package com.xxl.job.executor.service.jobhandler;

import cn.hutool.core.util.StrUtil;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import groovy.util.logging.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.luohuo.basic.jackson.JsonUtil;
import com.luohuo.basic.utils.ArgumentAssert;
import com.luohuo.flex.msg.biz.MsgBiz;
import com.luohuo.flex.msg.service.ExtendMsgService;

import java.util.Map;

@Component
@Slf4j
public class BaseJob {
    @Autowired
    private MsgBiz msgBiz;
    @Autowired
    private ExtendMsgService extendMsgService;

    @XxlJob("sendMsg")
    public void sendMsg() {
        String param = XxlJobHelper.getJobParam();
        ArgumentAssert.notEmpty(param, "参数不能为空");
        Map<String, String> map = JsonUtil.parse(param, Map.class);
        String msgIdStr = map.get("msgId");
        if (StrUtil.isEmpty(msgIdStr)) {
            return;
        }
        Long msgId = Long.valueOf(msgIdStr);
        XxlJobHelper.log("msgId={}", msgId);

        msgBiz.execSend(msgId);
    }

    @XxlJob("publishMsg")
    public void publishMsg() {
        String param = XxlJobHelper.getJobParam();
        ArgumentAssert.notEmpty(param, "参数不能为空");
        Map<String, String> map = JsonUtil.parse(param, Map.class);
        String msgIdStr = map.get("msgId");
        if (StrUtil.isEmpty(msgIdStr)) {
            return;
        }
        Long msgId = Long.valueOf(msgIdStr);
        XxlJobHelper.log(" msgId={}", msgId);

        extendMsgService.publishNotice(msgId);
    }
}
