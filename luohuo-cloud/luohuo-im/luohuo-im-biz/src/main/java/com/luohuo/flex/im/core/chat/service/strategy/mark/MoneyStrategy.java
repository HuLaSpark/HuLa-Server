package com.luohuo.flex.im.core.chat.service.strategy.mark;

import org.springframework.stereotype.Component;
import com.luohuo.flex.model.enums.MessageMarkTypeEnum;

/**
 * 红包标记策略
 * @author 乾乾
 */
@Component
public class MoneyStrategy extends AbstractMsgMarkStrategy {
    @Override
    protected MessageMarkTypeEnum getTypeEnum() {
        return MessageMarkTypeEnum.MONEY;
    }

//    @Override
//    public void doMark(Long uid, Long msgId) {
//        super.doMark(uid, msgId);
//        // 打赏时清除所有负面标记
//        MsgMarkFactory.getStrategyNoNull(MessageMarkTypeEnum.BOMB.getType()).unMark(uid, msgId);
//        MsgMarkFactory.getStrategyNoNull(MessageMarkTypeEnum.DISLIKE.getType()).unMark(uid, msgId);
//        MsgMarkFactory.getStrategyNoNull(MessageMarkTypeEnum.ANGRY.getType()).unMark(uid, msgId);
//    }
}