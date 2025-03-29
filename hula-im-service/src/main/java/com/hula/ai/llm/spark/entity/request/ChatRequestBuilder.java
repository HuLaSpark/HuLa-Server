package com.hula.ai.llm.spark.entity.request;

import com.hula.ai.llm.spark.enums.ModelEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * 讯飞星火 构建请求
 *
 * @author: 云裂痕
 * @date: 2023/09/06
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
public class ChatRequestBuilder {

    private final ChatRequest chatRequest;

    public ChatRequestBuilder() {
        chatRequest = new ChatRequest();

        // header
        chatRequest.setHeader(new ChatHeader());

        // parameter
        chatRequest.setParameter(new ChatParameter(new ChatCompletion()));

        // payload
        chatRequest.setPayload(new ChatPayload(new ChatMessage(new ArrayList<>())));
    }

    public ChatRequest build() {
        ModelEnum apiVersion = chatRequest.getApiVersion();
        chatRequest.getParameter().getChat().setDomain(apiVersion.getVersion());
        return chatRequest;
    }

    /**
     * 消息列表，如果想获取结合上下文的回答，需要将历史问答信息放在一起<br/>
     * 必传，消息列表总tokens不能超过8192
     */
    public ChatRequestBuilder messages(List<ChatCompletionMessage> messages) {
        chatRequest.getPayload().getMessage().setText(messages);
        return this;
    }

    /**
     * 核采样阈值。用于决定结果随机性,取值越高随机性越强即相同的问题得到的不同答案的可能性越高<br/>
     * 非必传,取值为[0,1],默认为0.5
     */
    public ChatRequestBuilder temperature(Double temperature) {
        chatRequest.getParameter().getChat().setTemperature(temperature);
        return this;
    }

    /**
     * 模型回答的tokens的最大长度<br/>
     * 非必传,取值为[1,4096],默认为2048
     */
    public ChatRequestBuilder maxTokens(Integer maxTokens) {
        chatRequest.getParameter().getChat().setMaxTokens(maxTokens);
        return this;
    }

    /**
     * 从k个候选中随机选择⼀个（⾮等概率）<br/>
     * 非必传,取值为[1,6],默认为4
     */
    public ChatRequestBuilder topK(Integer topK) {
        chatRequest.getParameter().getChat().setTopK(topK);
        return this;
    }

    /**
     * 每个用户的id，用于区分不同用户<br/>
     * 非必传，最大长度32
     */
    public ChatRequestBuilder uid(String uid) {
        chatRequest.getHeader().setUid(uid);
        return this;
    }

    /**
     * 用于关联用户会话<br/>
     * 非必传,需要保障用户下的唯一性
     */
    public ChatRequestBuilder chatId(String chatId) {
        chatRequest.getParameter().getChat().setChatId(chatId);
        return this;
    }

    /**
     * 覆盖默认的对话参数
     */
    public ChatRequestBuilder chatParameter(ChatCompletion chatParameter) {
        chatRequest.getParameter().setChat(chatParameter);
        return this;
    }

    /**
     * 指定apiVersion<br/>
     * 非必传，默认使用2.0版本
     */
    public ChatRequestBuilder apiVersion(ModelEnum apiVersion) {
        chatRequest.setApiVersion(apiVersion);
        return this;
    }

}
