package com.luohuo.flex.model.entity;

import lombok.Data;

/**
 * ws的基本返回信息体
 * @author nyh
 */
@Data
public class WsBaseResp<T> {
    /**
     * ws推送给前端的消息
     *
     * @see WSRespTypeEnum
     */
    private String type;
    private T data;
}
