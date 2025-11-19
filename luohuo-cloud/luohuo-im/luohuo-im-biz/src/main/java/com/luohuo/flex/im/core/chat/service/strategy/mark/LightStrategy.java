package com.luohuo.flex.im.core.chat.service.strategy.mark;

import org.springframework.stereotype.Component;
import com.luohuo.flex.model.enums.MessageMarkTypeEnum;

/**
 * 灯光标记策略
 * @author 乾乾
 */
@Component
public class LightStrategy extends AbstractMsgMarkStrategy {
    @Override
    protected MessageMarkTypeEnum getTypeEnum() {
        return MessageMarkTypeEnum.LIGHT;
    }
}