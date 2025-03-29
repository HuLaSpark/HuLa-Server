package com.hula.ai.llm.spark.entity.response;

import com.hula.ai.llm.spark.entity.request.ChatCompletionMessage;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

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
public class ChatResponseChoices implements Serializable {
    private static final long serialVersionUID = 3908073548592366629L;

    /**
     * 文本响应状态，取值为[0,1,2]; 0代表首个文本结果；1代表中间文本结果；2代表最后一个文本结果
     */
    private Integer status;

    /**
     * 返回的数据序号，取值为[0,9999999]
     */
    private Integer seq;

    /**
     * 消息列表
     */
    private List<ChatCompletionMessage> text;

}
