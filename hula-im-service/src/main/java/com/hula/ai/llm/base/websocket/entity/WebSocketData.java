package com.hula.ai.llm.base.websocket.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * websocket 转换对象
 *
 * @author: 云裂痕
 * @date: 2023/5/5
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketData {

    /**
     * 心跳码
     */
    private String functionCode;

    /**
     * 消息
     */
    private Object message;


}
