package com.luohuo.flex.im.core.chat.service.strategy.mark;

import org.springframework.stereotype.Component;
import com.luohuo.flex.model.enums.MessageMarkTypeEnum;

import java.util.List;

/**
 * 点赞标记策略类
 * @author nyh
 */
@Component
public class LikeStrategy extends AbstractMsgMarkStrategy {

    @Override
    protected MessageMarkTypeEnum getTypeEnum() {
        return MessageMarkTypeEnum.LIKE;
    }

    @Override
    public void doMark(Long uid, List<Long> uidList, Long msgId) {
        super.doMark(uid, uidList, msgId);
        //同时取消不满的动作
        MsgMarkFactory.getStrategyNoNull(MessageMarkTypeEnum.DISLIKE.getType()).unMark(uid, uidList, msgId);
//		MsgMarkFactory.getStrategyNoNull(MessageMarkTypeEnum.BOMB.getType()).unMark(uid, uidList, msgId);
    }
}
