package com.hula.ai.llm.base.key.updater;

import com.hula.ai.client.enums.ChatModelEnum;
import com.hula.ai.llm.wenxin.WenXinClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WenXinKeyUpdater implements KeyUpdater {
    @Autowired
    private WenXinClient wenXinClient;
    
    @Override
    public String supportModel() {
        return ChatModelEnum.WENXIN.getValue();
    }
    
    @Override
    public void updateKey(String key) {
        wenXinClient.setApiKey(key);
    }
}