package com.hula.ai.llm.moonshot.entity;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

/**
 * 模型
 *
 * @author: 云裂痕
 * @date: 2024/3/25
 * @version: 1.2.0
 * 得其道
 * 乾乾
 */
@Data
public class Model {

    private String id;
    private String object;

    @SerializedName("owner_by")
    private String ownedBy;
    private String root;
    private String parent;

    public Model(String id, String object, String ownedBy, String root, String parent) {
        this.id = id;
        this.object = object;
        this.ownedBy = ownedBy;
        this.root = root;
        this.parent = parent;
    }

}
