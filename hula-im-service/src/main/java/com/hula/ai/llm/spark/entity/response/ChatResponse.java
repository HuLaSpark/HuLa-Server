package com.hula.ai.llm.spark.entity.response;

import lombok.Data;

import java.io.Serializable;

/**
 * 讯飞星火 响应
 *
 * @author: 云裂痕
 * @date: 2023/09/06
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
@Data
public class ChatResponse implements Serializable {
    private static final long serialVersionUID = 886720558849587945L;

    private ChatResponseHeader header;

    private ChatResponsePayload payload;

}
