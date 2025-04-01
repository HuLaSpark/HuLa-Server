package com.hula.ai.llm.openai.entity.chat.tool;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ToolChoiceObjFunction {

    private String name;
}
