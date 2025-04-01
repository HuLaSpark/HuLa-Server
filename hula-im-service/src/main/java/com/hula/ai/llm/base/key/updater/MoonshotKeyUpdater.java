package com.hula.ai.llm.base.key.updater;

import com.hula.ai.client.enums.ChatModelEnum;
import com.hula.ai.llm.moonshot.MoonshotClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MoonshotKeyUpdater implements KeyUpdater {
    @Autowired
    private MoonshotClient moonshotClient;
    
    @Override
    public String supportModel() {
        return ChatModelEnum.MOONSHOT.getValue();
    }
    
    @Override
    public void updateKey(String key) {
        moonshotClient.setApiKey(key);
    }
}