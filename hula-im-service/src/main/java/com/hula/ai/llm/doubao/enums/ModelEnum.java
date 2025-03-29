package com.hula.ai.llm.doubao.enums;

import lombok.Getter;

/**
 * 豆包 API接口
 *
 * @author: 云裂痕
 * @date: 2025/2/16
 * @version: 1.2.4
 * 得其道
 * 乾乾
 */
@Getter
public enum ModelEnum {

    /**
     * 豆包 API
     * 1、开通模型服务
     * 2、创建在线推理
     * https://www.volcengine.com/docs/82379/1263272
     */
    LITE(""),

    PRO(""),

    ;

    private final String model;

    ModelEnum(String model) {
        this.model = model;
    }

}
