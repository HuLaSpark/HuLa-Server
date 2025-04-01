package com.hula.ai.llm.openai.entity.assistant.run;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.List;

@Data
@Slf4j
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CodeInterpreter implements Serializable {

    /**
     * 代码解释器工具调用的输入。
     */
    private String input;
    /**
     * 代码解释器工具调用的输出。
     * Code Interpreter可以输出一项或多项，包括文本（log）或图像（image）。
     * 其中每一个都由不同的对象类型表示。
     */
    private List<Output> outputs;

}
