package com.hula.ai.llm.spark.entity.response;

import lombok.Data;

import java.io.Serializable;

/**
 * 讯飞星火 返回体
 *
 * @author: 云裂痕
 * @date: 2023/09/06
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
@Data
public class ChatResponsePayload implements Serializable {
    private static final long serialVersionUID = 8090192271782303700L;

    /**
     * 返回内容
     */
    private ChatResponseChoices choices;

    /**
     * 使用信息
     */
    private ChatUsage usage;

}
