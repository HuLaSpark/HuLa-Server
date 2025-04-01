package com.hula.ai.llm.locallm.coze.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hula.ai.llm.locallm.base.BaseChatCompletion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Data
@Builder
@Slf4j
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class ChatCompletion extends BaseChatCompletion {

    /**
     * bot id
     */
    @JsonProperty("bot_id")
    private String botId;

    /**
     * 用户ID
     */
    @JsonProperty("user_id")
    private String userId;

    /**
     * 是否流式输出
     */
    @JsonProperty("stream")
    private Boolean stream;

    /**
     * 自定义参数 Bot中定义的变量
     */
    @JsonProperty("custom_variables")
    private Map<String, String> customVariables;

    /**
     * 是否保存本次对话记录
     */
    @JsonProperty("auto_save_history")
    private Boolean autoSaveHistory;

    /**
     * 附附加信息，通常用于封装一些业务相关的字段。查看对话消息详情时，系统会透传此附加信息。
     * 自定义键值对，应指定为 Map 对象格式。长度为 16 对键值对，其中键（key）的长度范围为 1～64 个字符，值（value）的长度范围为 1～512 个字符。
     */
    @JsonProperty("meta_data")
    private Map metaData;

    /**
     * 附加参数，通常用于特殊场景下指定一些必要参数供模型判断，例如指定经纬度，并询问 Bot 此位置的天气。
     * 自定义键值对格式，其中键（key）仅支持设置为：
     * latitude：纬度，此时值（Value）为纬度值，例如 39.9800718。
     * longitude：经度，此时值（Value）为经度值，例如 116.309314。
     */
    @JsonProperty("extra_params")
    private List<String> extraParams;

    /**
     * 聊天内容
     */
    @JsonProperty("additional_messages")
    private List<ChatCompletionMessage> additionalMessages;

}


