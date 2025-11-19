package com.luohuo.flex.im.core.chat.service.strategy.mark;

import org.springframework.stereotype.Component;
import com.luohuo.flex.model.enums.MessageMarkTypeEnum;

/**
 * 鲜花标记策略
 * @author 乾乾
 */
@Component
public class FlowerStrategy extends AbstractMsgMarkStrategy {
    @Override
    protected MessageMarkTypeEnum getTypeEnum() {
        return MessageMarkTypeEnum.FLOWER;
    }
}