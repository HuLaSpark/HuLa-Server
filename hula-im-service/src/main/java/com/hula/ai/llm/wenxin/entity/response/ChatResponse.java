package com.hula.ai.llm.wenxin.entity.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 返回内容
 *
 * @author: 云裂痕
 * @date: 2023/9/7
 * 得其道
 * 乾乾
 */
@Data
@ToString
public class ChatResponse implements Serializable {

    private String id;

    private String object;

    private Long created;

    @JsonProperty("sentence_id")
    private Long sentenceId;

    @JsonProperty("isEnd")
    private Boolean is_end;

    @JsonProperty("is_truncated")
    private Boolean isTruncated;

    private String result;

    @JsonProperty("need_clear_history")
    private Boolean needClearHistory;

    @JsonProperty("ban_round")
    private Long ban_round;

    private Usage usage;
}
