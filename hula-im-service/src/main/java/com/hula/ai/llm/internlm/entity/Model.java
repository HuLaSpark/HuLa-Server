package com.hula.ai.llm.internlm.entity;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
@NoArgsConstructor
@AllArgsConstructor
public class Model {

    private String id;
    private String object;
    private Long created;
    @SerializedName("owner_by")
    private String ownedBy;

}
