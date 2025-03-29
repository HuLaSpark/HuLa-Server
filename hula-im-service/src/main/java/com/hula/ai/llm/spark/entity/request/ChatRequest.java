package com.hula.ai.llm.spark.entity.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hula.ai.llm.spark.enums.ModelEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * 讯飞星火请求信息
 *
 * @author: 云裂痕
 * @date: 2024/05/30
 * @version: 1.1.7
 * 得其道
 * 乾乾
 */
@Data
public class ChatRequest implements Serializable {
    private static final long serialVersionUID = 8142547165395379456L;

    private ChatHeader header;

    private ChatParameter parameter;

    private ChatPayload payload;

    @JsonIgnore
    private transient ModelEnum apiVersion = ModelEnum.Lite;
    public static ChatRequestBuilder builder() {
        return new ChatRequestBuilder();
    }

}
