package com.hula.ai.llm.moonshot.entity;

import lombok.Data;

import java.util.List;

/**
 * 模型列表
 *
 * @author: 云裂痕
 * @date: 2024/3/25
 * @version: 1.2.0
 * 得其道
 * 乾乾
 */
@Data
public class ModelsList {

    /**
     * 模型数据
     */
    private List<Model> data;

}
