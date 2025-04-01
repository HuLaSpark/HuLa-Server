package com.hula.ai.llm.base.key.updater;

import com.hula.ai.client.enums.ChatModelEnum;
import com.hula.ai.llm.internlm.InternlmClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InternlmKeyUpdater implements KeyUpdater {
    @Autowired
    private InternlmClient internlmClient;
    
    @Override
    public String supportModel() {
        return ChatModelEnum.INTERNLM.getValue();
    }
    
    @Override
    public void updateKey(String key) {
        internlmClient.setToken(key);
    }
}