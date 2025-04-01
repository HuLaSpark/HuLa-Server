package com.hula.ai.gpt.pojo.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 *  大模型信息对象 VO
 *
 * @author: 云裂痕
 * @date: 2023-12-01
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
@Data
public class ModelVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 模型名称
     */
    private String name;

    /**
     * 模型logo
     */
    private String icon;

    /**
     * 模型名称
     */
    private String model;

    /**
     * 模型版本
     */
    private String version;

    /**
     * 本地模型类型：1、Langchian；2、ollama；3、Giteeai
     */
    private Integer localModelType;

    /**
     * 模型接口地址
     */
    private String modelUrl;

    /**
     * 知识库名称
     */
    private String knowledge;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态 0 禁用 1 启用
     */
    private Integer status;

}
