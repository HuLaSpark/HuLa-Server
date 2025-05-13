package com.hula.ai.controller.app;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.hula.ai.client.enums.ChatStatusEnum;
import com.hula.ai.client.model.command.ChatCommand;
import com.hula.ai.client.model.command.CompletionsParam;
import com.hula.ai.client.service.GptService;
import com.hula.ai.framework.validator.base.BaseAssert;
import com.hula.ai.gpt.pojo.param.ChatParam;
import com.hula.ai.gpt.pojo.vo.ChatMessageVO;
import com.hula.ai.gpt.pojo.vo.ChatVO;
import com.hula.ai.gpt.service.IChatMessageService;
import com.hula.ai.gpt.service.IChatService;
import com.hula.ai.llm.base.service.LLMService;
import com.hula.domain.vo.res.ApiResult;
import com.hula.utils.RequestHolder;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.awt.*;
import java.util.List;

/**
 * 对话接口
 *
 * @author: 云裂痕
 * @date: 2025/03/16
 * @version: 1.0.0
 * 得其道 乾乾
 */
@RestController(value = "ChatAiController")
@RequestMapping("/chat")
public class ChatController {
    @Autowired
    private IChatService chatService;
    @Autowired
    private IChatMessageService chatMessageService;
    @Autowired
    private GptService gptService;
    @Autowired
    private LLMService llmService;

    /**
     * 获取聊天列表
     *
	 * @author: 云裂痕
	 * @date: 2025/03/16
	 * @version: 1.0.0
	 */
    @PostMapping("list")
    public ApiResult listChat(@RequestBody ChatParam param) {
		param.setUid(RequestHolder.get().getUid());
        return ApiResult.success(chatService.listChat(param));
    }

    /**
     * 删除聊天列表
     *
	 * @author: 云裂痕
	 * @date: 2025/03/16
	 * @version: 1.0.0
	 */
    @PostMapping("/del/{chatNumber}")
    public ApiResult<Integer> deleteChat(@PathVariable("chatNumber") String chatNumber) {
        return ApiResult.success(chatService.removeChatByChatNumber(chatNumber));
    }

    /**
     * 获取聊天内容列表
     *
	 * @author: 云裂痕
	 * @date: 2025/03/16
	 * @version: 1.0.0
	 */
    @GetMapping("/message")
    public ApiResult<List<ChatMessageVO>> listChatMessage(ChatParam param) {
        BaseAssert.isBlankOrNull(param.getChatNumber(), "缺少会话标识");
		param.setStatus(ChatStatusEnum.SUCCESS.getValue());
        return ApiResult.success(chatMessageService.listChatMessage(param));
    }

    /**
     * 获取聊天内容
     *
	 * @author: 云裂痕
	 * @date: 2025/03/16
	 * @version: 1.0.0
	 */
    @GetMapping("/message/{conversationId}")
    public ApiResult listChatMessageById(@PathVariable String conversationId) {
        return ApiResult.returnResult("获取", ObjectUtil.isNotNull(chatMessageService.getChatByMessageId(conversationId)));
    }

    /**
     * 删除聊天内容
     *
	 * @author: 云裂痕
	 * @date: 2025/03/16
	 * @version: 1.0.0
	 */
    @PostMapping("/message/{conversationId}")
    public ApiResult removeChatMessageById(@PathVariable String conversationId) {
        return ApiResult.returnResult("删除", chatMessageService.removeChatMessageByMessageId(conversationId));
    }

    /**
     * 创建对话
     *
     * @author: 云裂痕
     * @date: 2025/03/16
     * @version: 1.0.0
     */
    @PostMapping
    public ApiResult<ChatVO> saveChat(@RequestBody ChatCommand command) {
        command.setUid(RequestHolder.get().getUid());
        return ApiResult.success(chatService.saveSSEChat(command));
    }

    /**
     * 发送消息
     *
	 * @author: 云裂痕
	 * @date: 2025/03/16
	 * @version: 1.0.0
	 */
    @PostMapping("/message")
    public ApiResult sendMessage(@Validated @RequestBody ChatCommand command) {
        command.setUid(RequestHolder.get().getUid());
        command.setOperater(command.getUid());
        command = gptService.validateGptCommand(command);
        return ApiResult.success(gptService.chatMessage(command));
    }

    /**
     * 创建sse连接
     * @return
     */
    @GetMapping("/sse/create")
    public SseEmitter createConnect() {
        return llmService.createSse(RequestHolder.get().getUid());
    }

    /**
     * 关闭连接
     */
    @GetMapping("/sse/close")
    public void closeConnect() {
        llmService.closeSse(RequestHolder.get().getUid());
    }

    /**
     * 对话响应
     *
	 * @author: 云裂痕
	 * @date: 2025/03/16
	 * @version: 1.0.0
	 */
    @PostMapping(value = "/completions", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public void completions(HttpServletResponse response, @RequestBody CompletionsParam completionsParam) {
        Boolean isWs = false;
        if (StrUtil.isNotEmpty(completionsParam.getWs())) {
            isWs = Boolean.valueOf(completionsParam.getWs());
        }
        llmService.sseChat(response, isWs, RequestHolder.get().getUid(), completionsParam);
    }

    /**
     * 同步响应
     *
	 * @author: 云裂痕
	 * @date: 2025/03/16
	 * @version: 1.0.0
	 */
    @PostMapping("/completions/sync")
    public ApiResult syncCompletions(@RequestBody ChatCommand command) {
        command.setUid(RequestHolder.get().getUid());
        return ApiResult.success(llmService.chat(command));
    }

}
