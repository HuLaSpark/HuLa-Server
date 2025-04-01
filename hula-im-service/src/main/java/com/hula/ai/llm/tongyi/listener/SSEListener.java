package com.hula.ai.llm.tongyi.listener;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.dashscope.aigc.generation.GenerationOutput;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.ResultCallback;
import com.alibaba.fastjson.JSON;
import com.hula.ai.client.enums.ChatContentEnum;
import com.hula.ai.client.enums.ChatModelEnum;
import com.hula.ai.client.enums.ChatRoleEnum;
import com.hula.ai.client.enums.ChatStatusEnum;
import com.hula.ai.client.model.command.ChatMessageCommand;
import com.hula.ai.client.service.GptService;
import com.hula.ai.framework.util.ApplicationContextUtil;
import com.hula.ai.llm.base.entity.ChatData;
import com.hula.ai.llm.base.websocket.WebsocketServer;
import com.hula.ai.llm.base.websocket.constant.FunctionCodeConstant;
import com.hula.ai.llm.base.websocket.entity.WebSocketData;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.Semaphore;

/**
 * 通义千问监听sse流式响应处理
 *
 * @author: 云裂痕
 * @date: 2023/12/4
 * 得其道
 * 乾乾
 */
@Slf4j
public class SSEListener extends ResultCallback<GenerationResult> {
    private Semaphore semaphore;
    private HttpServletResponse response;
    private Long chatId;
    private String parentMessageId;
    private String conversationId;
    private String finishReason;
    private String version;
    private Boolean error;
    private String errTxt;
    private String uid;
    private Boolean isWs = false;


    public SSEListener(HttpServletResponse response, Semaphore semaphore, Long chatId, String parentMessageId, String uid, String version, Boolean isWs) {
        this.response = response;
        this.semaphore = semaphore;
        this.chatId = chatId;
        this.parentMessageId = parentMessageId;
        if (!isWs) {
            this.response.setContentType(MediaType.TEXT_EVENT_STREAM_VALUE);
        }
        this.response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        this.response.setStatus(HttpStatus.OK.value());
        this.uid = uid;
        this.version = version;
        this.isWs = isWs;
        this.error = false;
    }

    @SneakyThrows
    @Override
    public void onEvent(GenerationResult result) {
        log.error("通义千问SSE返回，数据：{}", JSON.toJSONString(result));
        GenerationOutput.Choice choice = result.getOutput().getChoices().get(0);
        String text = choice.getMessage().getContent();
        if (ObjectUtil.isNotNull(choice.getFinishReason())) {
            finishReason = choice.getFinishReason();
            log.info("通义千问返回数据结束了");
            ChatMessageCommand chatMessage = ChatMessageCommand.builder().chatId(chatId).messageId(result.getRequestId()).parentMessageId(parentMessageId)
                    .model(ChatModelEnum.TONGYI.getValue()).modelVersion(version)
                    .content(choice.getMessage().getContent()).contentType(ChatContentEnum.TEXT.getValue()).role(ChatRoleEnum.ASSISTANT.getValue()).finishReason(finishReason)
                    .status(ChatStatusEnum.SUCCESS.getValue()).appKey("").usedTokens(Long.valueOf(result.getUsage().getOutputTokens() + result.getUsage().getInputTokens()))
                    .build();
            ApplicationContextUtil.getBean(GptService.class).saveChatMessage(chatMessage);
            return;
        }
        ChatData chatData = ChatData.builder().id(result.getRequestId()).conversationId(result.getRequestId())
                .parentMessageId(parentMessageId)
                .role(ChatRoleEnum.ASSISTANT.getValue()).content(text).build();
        if (isWs) {
            WebSocketData wsData = WebSocketData.builder().functionCode(FunctionCodeConstant.MESSAGE).message(chatData).build();
            WebsocketServer.sendMessageByUserId(uid, JSON.toJSONString(wsData));
        } else {
            response.getWriter().write(ObjectUtil.isNull(text) ? JSON.toJSONString(chatData) : "\n" + JSON.toJSONString(chatData));
            response.getWriter().flush();
        }
    }

    @Override
    public void onComplete() {
        log.error("通义千问输出完成");
        semaphore.release();
    }

    @SneakyThrows
    @Override
    public void onError(Exception e) {
        log.error("通义千问连接异常，异常：{}", e.getMessage());
        ChatData chatData = ChatData.builder().id(conversationId).conversationId(conversationId)
                .parentMessageId(parentMessageId).role(ChatRoleEnum.ASSISTANT.getValue()).content("通义千问接口请求失败，无法响应！")
                .contentType(ChatContentEnum.TEXT.getValue()).finish(true).build();
        this.error = true;
        this.errTxt = "通义千问接口连接异常";
        this.response.getWriter().write(JSON.toJSONString(chatData));
        this.response.getWriter().flush();
        semaphore.release();
    }

    public Boolean getError() {
        return error;
    }

    public String getErrTxt() {
        return errTxt;
    }

}
