package com.hula.ai.llm.locallm.gitee.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
    @Builder
    @Slf4j
    @NoArgsConstructor(force = true)
    @AllArgsConstructor
// 表示响应中的 choices 列表里的每个元素
    public class Choice {
        private int index;
        private Delta delta;
        private String finish_reason;
    }