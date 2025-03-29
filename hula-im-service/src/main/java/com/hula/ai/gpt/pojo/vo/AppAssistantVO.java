package com.hula.ai.gpt.pojo.vo;

import lombok.Data;

import java.io.Serializable;

/**
 *  AI助理功能对象 VO
 *
 * @author: 云裂痕
 * @date: 2023-04-28
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
@Data
public class AppAssistantVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;

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

}
