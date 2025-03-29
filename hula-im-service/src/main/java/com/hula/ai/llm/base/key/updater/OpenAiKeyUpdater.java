package com.hula.ai.llm.base.key.updater;

import com.hula.ai.client.enums.ChatModelEnum;
import com.hula.ai.llm.openai.OpenAiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class OpenAiKeyUpdater implements KeyUpdater {
    @Autowired
    private OpenAiClient openAiClient;
    
    @Override
    public String supportModel() {
        return ChatModelEnum.OPENAI.getValue();
    }
    
    @Override
    public void updateKey(String key) {
        openAiClient.setApiKey(Collections.singletonList(key));
    }
}