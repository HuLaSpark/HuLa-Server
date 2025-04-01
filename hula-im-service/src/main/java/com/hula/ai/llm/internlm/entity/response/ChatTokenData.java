package com.hula.ai.llm.internlm.entity.response;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

/**
 * 使用token
 *
 * @author: 云裂痕
 * @date: 2024/3/25
 * @version: 1.2.0
 * 得其道
 * 乾乾
 */
@Data
public class ChatTokenData {

    /**
     * 使用token
     */
    @SerializedName("total_tokens")
    public int totalTokens;


}
