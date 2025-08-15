package com.luohuo.flex.ws.rocketmq;

import com.luohuo.flex.ws.ReactiveContextUtil;
import org.apache.rocketmq.client.hook.SendMessageContext;
import org.apache.rocketmq.client.hook.SendMessageHook;

import static com.luohuo.basic.context.ContextConstants.HEADER_TENANT_ID;
import static com.luohuo.basic.context.ContextConstants.U_ID_HEADER;

/**
 * RocketMQ 消息队列的多租户 {@link SendMessageHook} 实现类
 *
 * Producer 发送消息时，将 {@link ReactiveContextUtil} 租户编号，添加到消息的 Header 中
 */
public class TenantRocketMQSendMessageHook implements SendMessageHook {

    @Override
    public String hookName() {
        return getClass().getSimpleName();
    }

    @Override
    public void sendMessageBefore(SendMessageContext sendMessageContext) {
        Long tenantId = ReactiveContextUtil.getTenantId();
		Long uid = ReactiveContextUtil.getUid();
		if (uid != null) {
			sendMessageContext.getMessage().putUserProperty(U_ID_HEADER, uid.toString());
		}
        if (tenantId != null) {
			sendMessageContext.getMessage().putUserProperty(HEADER_TENANT_ID, tenantId.toString());
        }
    }

    @Override
    public void sendMessageAfter(SendMessageContext sendMessageContext) {
    }

}
