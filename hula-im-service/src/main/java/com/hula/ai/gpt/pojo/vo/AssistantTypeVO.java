package com.hula.ai.gpt.pojo.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 *  助手分类对象 VO
 *
 * @author: 云裂痕
 * @date: 2023-11-22
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
@Data
public class AssistantTypeVO implements Serializable {

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
     * 排序
     */
    private Integer sort;

    /**
     * 分类名称
     */
    private String name;

    /**
     * icon图标
     */
    private String icon;

    /**
     * 状态 0 禁用 1 启用
     */
    private Integer status;

}
