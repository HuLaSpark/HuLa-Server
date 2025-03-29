package com.hula.ai.llm.locallm.coze.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class ChatCompletionMessage implements Serializable {

    @NotNull
    private String role;

    /**
     * 消息类型。默认为 question。
     * question：用户输入内容。
     * answer：Bot 返回给用户的消息内容，支持增量返回。如果工作流绑定了消息节点，可能会存在多 answer 场景，此时可以用流式返回的结束标志来判断所有 answer 完成。
     * function_call：Bot 对话过程中调用函数（function call）的中间结果。
     * tool_output：调用工具 （function call）后返回的结果。
     * tool_response：调用工具 （function call）后返回的结果。
     * follow_up：如果在 Bot 上配置打开了用户问题建议开关，则会返回推荐问题相关的回复内容。不支持在请求中作为入参。
     * verbose：多 answer 场景下，服务端会返回一个 verbose 包，对应的 content 为 JSON 格式，content.msg_type =generate_answer_finish 代表全部 answer 回复完成。不支持在请求中作为入参。
     */
    private String type;

    /**
     * 消息的内容，支持纯文本、多模态（文本、图片、文件混合输入）、卡片等多种类型的内容。
     * content_type 为 object_string 时，content 为 object_string object 数组序列化之后的 JSON String，详细说明可参考 object_string object。
     * 当 content_type = text 时，content 为普通文本，例如 "content" :"Hello!"。
     */
    private String content;

    /**
     * 消息内容的类型，支持设置为：
     * text：文本。
     * object_string：多模态内容，即文本和文件的组合、文本和图片的组合。
     * card：卡片。此枚举值仅在接口响应中出现，不支持作为入参。
     * content 不为空时，此参数为必选。
     */
    @JsonProperty("content_type")
    private String contentType;

    /**
     * 创建消息时的附加消息，获取消息时也会返回此附加消息。
     * 自定义键值对，应指定为 Map 对象格式。长度为 16 对键值对，其中键（key）的长度范围为 1～64 个字符，值（value）的长度范围为 1～512 个字符。
     */
    @JsonProperty("meta_data")
    private Map metaData;

    public static Builder builder() {
        return new Builder();
    }

    private ChatCompletionMessage(Builder builder) {
        setRole(builder.role);
        setContent(builder.content);
        setContentType(builder.contentType);
    }

    @Getter
    @AllArgsConstructor
    public enum Role {
        USER("user"),
        ASSISTANT("assistant");
        private String name;
    }

    public static final class Builder {
        private @NotNull String role;
        private String content;
        private String contentType;

        public Builder() {
        }

        public Builder role(@NotNull String role) {
            this.role = role;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder contentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public ChatCompletionMessage build() {
            return new ChatCompletionMessage(this);
        }
    }

}
