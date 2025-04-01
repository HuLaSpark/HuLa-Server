package com.hula.ai.llm.base.key.factory;

import com.hula.ai.llm.base.key.updater.KeyUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 密钥更新器工厂
 * 用于管理和获取各种模型的密钥更新器
 */
@Component
public class KeyUpdaterFactory {
    private static final Logger log = LoggerFactory.getLogger(KeyUpdaterFactory.class);
    
    private final Map<String, KeyUpdater> keyUpdaterMap = new HashMap<>();
    
    /**
     * 构造函数自动注入所有KeyUpdater实现
     * @param keyUpdaters 所有KeyUpdater实现的列表
     */
    @Autowired
    public KeyUpdaterFactory(List<KeyUpdater> keyUpdaters) {
        keyUpdaters.forEach(updater -> {
            String modelType = updater.supportModel();
            keyUpdaterMap.put(modelType, updater);
            log.info("已注册模型[{}]的密钥更新器: {}", modelType, updater.getClass().getSimpleName());
        });
    }
    
    /**
     * 获取对应模型的密钥更新器
     * @param model 模型类型
     * @return 对应的更新器
     * @throws IllegalArgumentException 如果找不到对应的更新器
     */
    public KeyUpdater getKeyUpdater(String model) {
        KeyUpdater updater = keyUpdaterMap.get(model);
        if (updater == null) {
            log.error("找不到模型[{}]的密钥更新器", model);
            throw new IllegalArgumentException("不支持的模型类型: " + model);
        }
        return updater;
    }
    
    /**
     * 查询当前支持的所有模型类型
     * @return 所有支持的模型类型列表
     */
    public List<String> getSupportedModels() {
        return (List<String>) keyUpdaterMap.keySet();
    }

}