package com.hula.ai.llm.moonshot.constant;

/**
 * 月之暗面 接口常量
 *
 * @author: 云裂痕
 * @date: 2025/03/08
 * @version: 1.2.8
 * 得其道 乾乾
 */
public interface ApiConstant {

    String BASE_COMPLETION_URL = "https://api.moonshot.cn/v1";

    /**
     * 对话
     */
    String CHAT_COMPLETION_URL = BASE_COMPLETION_URL + "/chat/completions";

    /**
     * 计算token
     */
    String ESTIMATE_TOKEN_COUNT_URL = BASE_COMPLETION_URL + "/tokenizers/estimate-token-count";

    /**
     * 获取模型列表
     */
    String CHAT_LIST_MODELS_URL = BASE_COMPLETION_URL + "/models";

    /**
     * 文件列表
     */
    String UPLOAD_FILES_URL = BASE_COMPLETION_URL + "/files";
}
