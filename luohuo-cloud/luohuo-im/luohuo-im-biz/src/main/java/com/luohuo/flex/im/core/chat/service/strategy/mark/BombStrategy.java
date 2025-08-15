package com.luohuo.flex.im.core.chat.service.strategy.mark;

import org.springframework.stereotype.Component;
import com.luohuo.flex.model.enums.MessageMarkTypeEnum;

import java.util.List;

/**
 * 炸弹标记策略
 * @author 乾乾
 */
@Component
public class BombStrategy extends AbstractMsgMarkStrategy {
    @Override
    protected MessageMarkTypeEnum getTypeEnum() {
        return MessageMarkTypeEnum.BOMB;
    }

    @Override
    public void doMark(Long uid, List<Long> uidList, Long msgId) {
        super.doMark(uid, uidList, msgId);
        // 使用炸弹时清除正向标记
        MsgMarkFactory.getStrategyNoNull(MessageMarkTypeEnum.LIKE.getType()).unMark(uid, uidList, msgId);
        MsgMarkFactory.getStrategyNoNull(MessageMarkTypeEnum.MONEY.getType()).unMark(uid, uidList, msgId);
    }
}