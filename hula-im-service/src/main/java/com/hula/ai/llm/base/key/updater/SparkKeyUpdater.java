package com.hula.ai.llm.base.key.updater;

import com.hula.ai.client.enums.ChatModelEnum;
import com.hula.ai.llm.spark.SparkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SparkKeyUpdater implements KeyUpdater {
    @Autowired
    private SparkClient sparkClient;
    
    @Override
    public String supportModel() {
        return ChatModelEnum.SPARK.getValue();
    }
    
    @Override
    public void updateKey(String key) {
        sparkClient.setApiKey(key);
    }
}