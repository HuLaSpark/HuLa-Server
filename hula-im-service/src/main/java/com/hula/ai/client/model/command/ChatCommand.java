package com.hula.ai.client.model.command;

import com.hula.ai.common.api.CommonCommand;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

/**
 * 对话消息对象 Command
 *
 * @author: 云裂痕
 * @date: 2025-03-08
 * @version: 1.2.8
 * 得其道 乾乾
 */
@Data
public class ChatCommand extends CommonCommand implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 聊天编号
     */
    @NotBlank(message = "缺少对话标识")
    private String chatNumber;

    /**
     * 对话id
     */
    private String conversationId;

    /**
     * 用户id
     */
	private Long uid;

    /**
     * 角色模型id
     */
    private Long assistantId;

    /**
     * 系统提示
     */
    private String systemPrompt;

    /**
     * 提示
     */
    @NotBlank(message = "缺少提示词")
    private String prompt;

    /**
     * 使用模型
     */
    @NotBlank(message = "缺少模型信息")
    private String model;

    /**
     * 使用模型版本
     */
    private String modelVersion;

    /**
     * 是否api请求
     */
    private Boolean api;

    /**
     * 聊天内容
     */
    private List<ChatMessageCommand> messages;
}
