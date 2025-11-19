package com.luohuo.flex.im.core.chat.service.strategy.mark;

import com.luohuo.basic.exception.code.GroupErrorEnum;
import com.luohuo.basic.validator.utils.AssertUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 消息标记策略工厂
 * @author nyh
 */
public class MsgMarkFactory {
    private static final Map<Integer, AbstractMsgMarkStrategy> STRATEGY_MAP = new HashMap<>();

    public static void register(Integer markType, AbstractMsgMarkStrategy strategy) {
        STRATEGY_MAP.put(markType, strategy);
    }

    public static AbstractMsgMarkStrategy getStrategyNoNull(Integer markType) {
        AbstractMsgMarkStrategy strategy = STRATEGY_MAP.get(markType);
        AssertUtil.isNotEmpty(strategy, GroupErrorEnum.PARAM_VALID, "strategy");
        return strategy;
    }
}
