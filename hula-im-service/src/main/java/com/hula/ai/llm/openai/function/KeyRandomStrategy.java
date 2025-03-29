package com.hula.ai.llm.openai.function;

import cn.hutool.core.util.RandomUtil;

import java.util.List;

/**
 * 描述：随机策略
 *
 */
public class KeyRandomStrategy implements KeyStrategyFunction<List<String>, String> {

    @Override
    public String apply(List<String> apiKeys) {
        return RandomUtil.randomEle(apiKeys);
    }
}
