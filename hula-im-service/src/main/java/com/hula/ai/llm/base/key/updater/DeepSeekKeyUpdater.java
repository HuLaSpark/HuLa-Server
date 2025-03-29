package com.hula.ai.llm.base.key.updater;

import com.hula.ai.client.enums.ChatModelEnum;
import com.hula.ai.llm.deepseek.DeepSeekStreamClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class DeepSeekKeyUpdater implements KeyUpdater {
    @Autowired
    private DeepSeekStreamClient deepSeekStreamClient;
    
    @Override
    public String supportModel() {
        return ChatModelEnum.DEEPSEEK.getValue();
    }
    
    @Override
    public void updateKey(String key) {
        deepSeekStreamClient.setApiKey(Collections.singletonList(key));
    }
}