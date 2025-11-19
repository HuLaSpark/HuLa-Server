package com.luohuo.flex.im.core.chat.service.strategy.mark;

import com.luohuo.flex.model.enums.MessageMarkTypeEnum;
import org.springframework.stereotype.Component;

/**
 * 愤怒标记策略
 * @author 乾乾
 */
@Component
public class AngryStrategy extends AbstractMsgMarkStrategy {
    @Override
    protected MessageMarkTypeEnum getTypeEnum() {
        return MessageMarkTypeEnum.ANGRY;
    }

//    @Override
//    public void doMark(Long uid, Long msgId) {
//        super.doMark(uid, msgId);
//        // 标记愤怒时清除正向表情
//        MsgMarkFactory.getStrategyNoNull(MessageMarkTypeEnum.LIKE.getType()).unMark(uid, msgId);
//        MsgMarkFactory.getStrategyNoNull(MessageMarkTypeEnum.HEART.getType()).unMark(uid, msgId);
//		MsgMarkFactory.getStrategyNoNull(MessageMarkTypeEnum.MONEY.getType()).unMark(uid, msgId);
//    }
}