package com.hula.ai.llm.tongyi;

import lombok.Data;

/**
 * 通义千问client
 * 文档地址：https://help.aliyun.com/zh/dashscope/developer-reference/model-square/?disableWebsiteRedirect=true
 *
 * @author: 云裂痕
 * @date: 2023/12/4
 * 得其道
 * 乾乾
 */
@Data
public class TongYiClient {

    private String appKey;

    public TongYiClient() {
    }

    public TongYiClient(String appKey) {
        this.appKey = appKey;
    }

}
