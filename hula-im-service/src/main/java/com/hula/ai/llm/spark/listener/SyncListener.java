package com.hula.ai.llm.spark.listener;

import com.hula.ai.llm.spark.constant.StatusConstant;
import com.hula.ai.llm.spark.entity.request.ChatRequest;
import com.hula.ai.llm.spark.entity.response.ChatResponse;
import com.hula.ai.llm.spark.entity.response.ChatSyncResponse;
import com.hula.ai.llm.spark.entity.response.ChatUsage;
import okhttp3.Response;
import okhttp3.WebSocket;

import javax.validation.constraints.NotNull;

/**
 * 讯飞星火同步回答监听
 *
 * @author: 云裂痕
 * @date: 2023/09/06
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
public class SyncListener extends BaseListener {

    private final StringBuilder stringBuilder = new StringBuilder();

    private final ChatSyncResponse chatSyncResponse;

    public SyncListener(ChatSyncResponse chatSyncResponse) {
        this.chatSyncResponse = chatSyncResponse;
    }

    @Override
    public void onMessage(String content, ChatUsage usage, Integer status, ChatRequest chatRequest, ChatResponse chatResponse, WebSocket webSocket) {
        stringBuilder.append(content);
        if (StatusConstant.FINISH == status) {
            chatSyncResponse.setContent(stringBuilder.toString());
            chatSyncResponse.setTextUsage(usage.getText());
            chatSyncResponse.setOk(true);
        }
    }

    @Override
    public void onError(@NotNull WebSocket webSocket, @NotNull Throwable t, Response response) {
        chatSyncResponse.setErrTxt(t.getMessage());
        chatSyncResponse.setSuccess(false);
    }

}
