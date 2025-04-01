package com.hula.ai.llm.spark.entity.response;

import lombok.Data;

import java.io.Serializable;

/**
 * 讯飞星火 返回响应头
 *
 * @author: 云裂痕
 * @date: 2023/09/06
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
@Data
public class ChatResponseHeader implements Serializable {
    private static final long serialVersionUID = -2828057068263022569L;

    /**
     * 错误码，0表示正常，非0表示出错；详细释义可在接口说明文档最后的错误码说明了解
     */
    private Integer code;

    /**
     * 会话状态，取值为[0,1,2]；0代表首次结果；1代表中间结果；2代表最后一个结果
     */
    private Integer status;

    /**
     * 会话是否成功的描述信息
     */
    private String message;

    /**
     * 会话的唯一id，用于讯飞技术人员查询服务端会话日志使用,出现调用错误时建议留存该字段
     */
    private String sid;

}
