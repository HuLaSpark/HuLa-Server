package com.hula.ai.llm.base.key.updater;

import com.hula.ai.client.enums.ChatModelEnum;
import com.hula.ai.llm.chatglm.ChatGLMClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ChatGLMKeyUpdater implements KeyUpdater {
    @Autowired
    private ChatGLMClient chatGLMClient;
    
    @Override
    public String supportModel() {
        return ChatModelEnum.CHATGLM.getValue();
    }
    
    @Override
    public void updateKey(String key) {
        chatGLMClient.setAppKey(key);
    }
}