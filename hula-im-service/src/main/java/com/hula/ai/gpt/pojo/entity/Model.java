package com.hula.ai.gpt.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 *  大模型信息对象 gpt_model
 *
 * @author: 云裂痕
 * @date: 2023-12-01
 * @version: 1.0.0
 * 得其道 乾乾
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("ai_gpt_model")
public class Model implements Serializable {

    private static final long serialVersionUID = 1L;

	@TableId(value = "id", type = IdType.ASSIGN_ID)
	protected Long id;

	@Schema(description = "创建时间")
	protected LocalDateTime createdTime;

	@Schema(description = "创建人ID")
	protected Long createdBy;

	@Schema(description = "最后修改时间")
	protected LocalDateTime updatedTime;

	@Schema(description = "最后修改人ID")
	protected Long updatedBy;

	@JsonIgnore
	@TableLogic(value = "false", delval = "true")
	protected Boolean isDel;

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
     * 本地模型类型：1、Langchian；2、ollama；3、Giteeai 4、Coze
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
