package com.hula.ai.llm.spark.enums;

import lombok.Getter;

/**
 * 讯飞星火 API接口
 *
 * @author: 云裂痕
 * @date: 2023/09/06
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
@Getter
public enum ModelEnum {

    /**
     * 星火大模型API当前有Lite、Pro、Pro-128K、Max、Max-32K和4.0 Ultra六个版本 和 科技文献大模型（kjwx），各版本独立计量tokens
     * https://www.xfyun.cn/doc/spark/Web.html#_1-%E6%8E%A5%E5%8F%A3%E8%AF%B4%E6%98%8E
     */
    Lite( "https://spark-api.xf-yun.com/v1.1/chat", "lite"),

    PRO( "https://spark-api.xf-yun.com/v3.1/chat", "generalv3"),
    PRO_128K( "https://spark-api.xf-yun.com/chat/pro-128k", "pro-128k"),
    MAX("https://spark-api.xf-yun.com/v3.5/chat", "generalv3.5"),
    MAX_32K( "https://spark-api.xf-yun.com/chat/max-32k", "max-32k"),
    ULTRA( "https://spark-api.xf-yun.com/v4.0/chat", "4.0Ultra"),
    ;


    private final String url;

    private final String version;

    ModelEnum( String url, String version) {
        this.url = url;
        this.version = version;
    }

    public static ModelEnum getEnum(String version) {
        for (ModelEnum value : ModelEnum.values()) {
            if (version.equals(value.version)) {
                return value;
            }
        }
        return null;
    }

}
