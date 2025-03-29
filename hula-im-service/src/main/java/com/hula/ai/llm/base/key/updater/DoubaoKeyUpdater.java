package com.hula.ai.llm.base.key.updater;

import com.hula.ai.client.enums.ChatModelEnum;
import com.hula.ai.llm.doubao.DouBaoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DoubaoKeyUpdater implements KeyUpdater {
    @Autowired
    private DouBaoClient douBaoClient;
    
    @Override
    public String supportModel() {
        return ChatModelEnum.DOUBAO.getValue();
    }
    
    @Override
    public void updateKey(String key) {
        douBaoClient.setApiKey(key);
    }
}