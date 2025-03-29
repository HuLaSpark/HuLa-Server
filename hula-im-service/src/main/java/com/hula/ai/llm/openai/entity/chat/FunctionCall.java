package com.hula.ai.llm.openai.entity.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 描述：函数调用返回值
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FunctionCall {
    /**
     * 方法名
     */
    private String name;
    /**
     * 方法参数
     */
    private String arguments;
}
