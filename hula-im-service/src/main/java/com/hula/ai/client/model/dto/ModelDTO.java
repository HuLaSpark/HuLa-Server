package com.hula.ai.client.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 大模型信息对象 DTO
 *
 * @author: 云裂痕
 * @date: 2023-12-01
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
@Data
public class ModelDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 创建人
     */
    private Long createdBy;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 更新人
     */
    private String updateUser;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

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
     * 状态 0 禁用 1 启用
     */
    private Integer status;

}
