package com.luohuo.flex.dto;

import lombok.Data;

@Data
public class TranslateRequest {
    private String text;
    private String provider; // youdao / tencent，可选
    private String source;   // 源语言，默认 auto
    private String target;   // 目标语言，默认 zh 或 zh-CHS
}
