package com.luohuo.flex.im.core.chat.service.strategy.mark;

import com.luohuo.flex.model.enums.MessageMarkTypeEnum;
import org.springframework.stereotype.Component;

/**
 * 礼炮标记策略
 * @author 乾乾
 */
@Component
public class CelebrateStrategy extends AbstractMsgMarkStrategy {
    @Override
    protected MessageMarkTypeEnum getTypeEnum() {
        return MessageMarkTypeEnum.CELEBRATE;
    }
}