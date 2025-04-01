package com.hula.ai.llm.locallm.gitee.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Data
@Builder
@Slf4j
@NoArgsConstructor(force = true)
@AllArgsConstructor
// 表示整个响应的主类
public class ChatCompletionResponse {
    private String id;
    private String object;
    private long created;
    private String model;
    private List<Choice> choices;
}