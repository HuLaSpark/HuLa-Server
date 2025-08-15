package com.luohuo.flex.im.core.chat.service.strategy.mark;

import com.luohuo.flex.model.enums.MessageMarkTypeEnum;
import org.springframework.stereotype.Component;

/**
 * 胜利标记策略
 * @author 乾乾
 */
@Component
public class VictoryStrategy extends AbstractMsgMarkStrategy {
    @Override
    protected MessageMarkTypeEnum getTypeEnum() {
        return MessageMarkTypeEnum.VICTORY;
    }
}