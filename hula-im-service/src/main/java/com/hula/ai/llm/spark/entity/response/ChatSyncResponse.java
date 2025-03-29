package com.hula.ai.llm.spark.entity.response;

import lombok.Data;

import java.io.Serializable;

/**
 * 讯飞星火 响应内容
 *
 * @author: 云裂痕
 * @date: 2023/09/06
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
@Data
public class ChatSyncResponse implements Serializable {
    private static final long serialVersionUID = -6785055441385392782L;

    /**
     * 回答内容
     */
    private String content;

    /**
     * tokens统计
     */
    private Usage textUsage;

    /**
     * 内部自用字段
     */
    private boolean ok = false;

    /**
     * 成功状态
     */
    private boolean success = false;

    /**
     * 错误原因
     */
    private String errTxt;

}
