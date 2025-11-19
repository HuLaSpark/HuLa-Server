package com.luohuo.flex.im.core.chat.service.strategy.msg;

import com.luohuo.basic.exception.code.GroupErrorEnum;
import com.luohuo.basic.validator.utils.AssertUtil;

import java.util.HashMap;
import java.util.Map;


/**
 * @author nyh
 */
public class MsgHandlerFactory {
    private static final Map<Integer, AbstractMsgHandler> STRATEGY_MAP = new HashMap<>();

    public static void register(Integer code, AbstractMsgHandler strategy) {
        STRATEGY_MAP.put(code, strategy);
    }

    public static AbstractMsgHandler getStrategyNoNull(Integer code) {
        AbstractMsgHandler strategy = STRATEGY_MAP.get(code);
        AssertUtil.isNotEmpty(strategy, GroupErrorEnum.PARAM_VALID.getMsg());
        return strategy;
    }
}
