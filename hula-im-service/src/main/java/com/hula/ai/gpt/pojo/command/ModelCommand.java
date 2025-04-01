package com.hula.ai.gpt.pojo.command;

import com.hula.ai.common.api.CommonCommand;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 *  大模型信息对象 Command
 *
 * @author: 云裂痕
 * @date: 2023-12-01
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
@Data
public class ModelCommand extends CommonCommand implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 模型名称
     */
    @NotBlank(message = "模型名称不能为空")
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
