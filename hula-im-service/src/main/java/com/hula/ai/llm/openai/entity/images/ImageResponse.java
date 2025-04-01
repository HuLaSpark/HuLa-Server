package com.hula.ai.llm.openai.entity.images;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 描述：
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImageResponse implements Serializable {
    private Long created;
    private List<Item> data;
}
