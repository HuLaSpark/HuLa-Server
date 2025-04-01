package com.hula.ai.llm.openai.entity.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 描述：
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Model implements Serializable {

    private String id;
    private String object;
    private long created;
    @JsonProperty("owned_by")
    private String ownedBy;
    @JsonProperty("permission")
    private List<Permission> permission;
    private String root;
    private Object parent;
}
