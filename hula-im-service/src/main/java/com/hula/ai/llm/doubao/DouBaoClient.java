package com.hula.ai.llm.doubao;

import com.hula.ai.llm.doubao.listener.SSEListener;
import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionChunk;
import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionRequest;
import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionResult;
import com.volcengine.ark.runtime.service.ArkService;
import io.reactivex.Flowable;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * 豆包client
 * 文档地址：https://www.volcengine.com/docs/82379/1099455
 *
 * @author: 云裂痕
 * @date: 2025/2/16
 * 得其道
 * 乾乾
 */
@Data
public class DouBaoClient {

    @Getter
    @Setter
    private String apiKey;

    public DouBaoClient() {
    }

    public DouBaoClient(String apiKey) {
        this.apiKey = apiKey;
    }

    private DouBaoClient(Builder builder) {
        apiKey = builder.apiKey;
    }

    /**
     * 同步响应
     *
     * @param request
     */
    public ChatCompletionResult chat(ChatCompletionRequest request) {
        ArkService service = ArkService.builder().apiKey(apiKey).build();
        ChatCompletionResult invokeModelApiResp = service.createChatCompletion(request);
        try {
            return invokeModelApiResp;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 流式响应
     *
     * @param request
     */
    public Boolean streamChat(HttpServletResponse response, ChatCompletionRequest request, Long chatId, String parentMessageId, String version, String uid, Boolean isWs) {
        ArkService service = ArkService.builder().apiKey(apiKey).build();
        SSEListener sseListener = new SSEListener(response, chatId, parentMessageId, version, uid, isWs);
        Flowable<ChatCompletionChunk> chunks =  service.streamChatCompletion(request);
        Boolean flag = sseListener.streamChat(chunks);
        // shutdown service
        service.shutdownExecutor();
        return flag;
    }


    /**
     * 构造
     *
     * @return
     */
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String apiKey;

        public Builder() {
        }

        public Builder apiKey(String val) {
            apiKey = val;
            return this;
        }

        public DouBaoClient build() {
            return new DouBaoClient(this);
        }
    }

}
