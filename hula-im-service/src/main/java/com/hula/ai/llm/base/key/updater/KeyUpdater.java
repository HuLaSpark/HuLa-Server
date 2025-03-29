package com.hula.ai.llm.base.key.updater;

// 1. 首先创建一个统一的密钥更新器接口
public interface KeyUpdater {

    // 返回支持的模型类型
    String supportModel();

    // 更新密钥方法
    void updateKey(String key);
}