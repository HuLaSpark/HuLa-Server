package com.hula.ai.gpt.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 聊天摘要对象 VO
 *
 * @author: 云裂痕
 * @date: 2023-04-28
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatVO implements Serializable {

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
     * 聊天编号
     */
    private String chatNumber;

    /**
     * 角色id
     */
    private Long assistantId;
    private String assistantTitle;
    private String assistantIcon;
    private String assistantAvatar;

    /**
     * 提示词
     */
    private String prompt;

    /**
     * 会员id
     */
    private Long uid;

    /**
     * 会员昵称
     */
    private String userName;

    /**
     * 聊天摘要
     */
    private String title;

}
