package com.hula.ai.llm.locallm.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class BaseChatCompletion implements Serializable {

    private Boolean stream = false;

}


