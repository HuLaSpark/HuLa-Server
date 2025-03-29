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
// 表示 Choice 中的 delta 部分
    public class Delta {
        private String role;
        private String content;
    }