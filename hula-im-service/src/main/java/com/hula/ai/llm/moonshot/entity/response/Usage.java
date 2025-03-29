package com.hula.ai.llm.moonshot.entity.response;

import lombok.Data;

/**
 * 使用量
 *
 * @author: 云裂痕
 * @date: 2024/3/25
 * @version: 1.2.0
 * 得其道
 * 乾乾
 */
@Data
public class Usage {

    /**
     * 提示词token
     */
    private int promptTokens;

    /**
     * 输出token
     */
    private int completionTokens;

    /**
     * 总token
     */
    private int totalTokens;

}
