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
 *  AI助理功能对象 gpt_assistant
 *
 * @author: 云裂痕
 * @date: 2023-04-28
 * @version: 1.0.0
 * 得其道 乾乾
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("ai_gpt_assistant")
public class Assistant implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 角色图标
     */
    private String icon;

    /**
     * 角色名称
     */
    private String title;

    /**
     * 标签
     */
    private String tag;

    /**
     * 角色描述
     */
    private String description;

    /**
     * 系统提示词
     */
    private String systemPrompt;

    /**
     * AI打招呼
     */
    private String firstMessage;

    /**
     * 主模型
     */
    private Integer mainModel;

    /**
     * 分类id
     */
    private Long typeId;

    /**
     * 排序
     */
    private Long sort;

    private Integer status;

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
}
