package com.luohuo.flex.ws.rocketmq;

import com.luohuo.flex.ws.ReactiveContextUtil;
import org.apache.rocketmq.client.hook.ConsumeMessageContext;
import org.apache.rocketmq.client.hook.ConsumeMessageHook;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.messaging.handler.invocation.InvocableHandlerMethod;

import java.util.List;

import static com.luohuo.basic.context.ContextConstants.HEADER_TENANT_ID;
import static com.luohuo.basic.context.ContextConstants.U_ID_HEADER;


/**
 * RocketMQ 消息队列的多租户 {@link ConsumeMessageHook} 实现类
 *
 * Consumer 消费消息时，将消息的 Header 的租户编号，添加到 {@link ReactiveContextUtil} 中，通过 {@link InvocableHandlerMethod} 实现
 */
public class TenantRocketMQConsumeMessageHook implements ConsumeMessageHook {

    @Override
    public String hookName() {
        return getClass().getSimpleName();
    }

	/**
	 * 不支持批量消费, 只能是 public class ClassA implements RocketMQListener<ClassDTO> 的形式
	 * @param context
	 */
    @Override
    public void consumeMessageBefore(ConsumeMessageContext context) {
        // 校验，消息必须是单条，不然设置租户可能不正确
        List<MessageExt> messages = context.getMsgList();
        // 设置租户编号、登录用户信息
		MessageExt messageExt = messages.get(0);
		String uid = messageExt.getUserProperty(U_ID_HEADER);
		String tenantId = messageExt.getUserProperty(HEADER_TENANT_ID);
		if(uid != null){
			ReactiveContextUtil.setUid(Long.parseLong(uid));
		}
		if(tenantId != null){
			ReactiveContextUtil.setTenantId(Long.parseLong(tenantId));
		}
    }

	/**
	 * 绝对清理租户上下文
	 * @param context
	 */
    @Override
    public void consumeMessageAfter(ConsumeMessageContext context) {
		try {} finally {
			ReactiveContextUtil.remove();
		}
    }

}
