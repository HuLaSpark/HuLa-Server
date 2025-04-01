package com.hula.ai.llm.openai.entity.billing;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 描述：金额消耗列表
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DailyCost {
    /**
     * 时间戳
     */
    @JsonProperty("timestamp")
    private long timestamp;
    /**
     * 模型消耗金额详情
     */
    @JsonProperty("line_items")
    private List<LineItem> lineItems;
}
