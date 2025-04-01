package com.hula.ai.llm.base.key.updater;

import com.hula.ai.client.enums.ChatModelEnum;
import com.hula.ai.llm.locallm.LocalLMClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LocalLMKeyUpdater implements KeyUpdater {
    @Autowired
    private LocalLMClient localLMClient;
    
    @Override
    public String supportModel() {
        return ChatModelEnum.LOCALLM.getValue();
    }
    
    @Override
    public void updateKey(String key) {
        localLMClient.setApiKey(key);
    }
}