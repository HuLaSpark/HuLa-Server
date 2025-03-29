package com.hula.ai.llm.locallm.coze.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 多模态内容
 *
 * @author: 云裂痕
 * @date: 2023/12/4
 * @version: 1.0.0
 * Copyright Ⓒ 2023 Master Computer Corporation Limited All rights reserved.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class ChatObjectContent implements Serializable {

    /**
     * 多模态消息内容类型，支持设置为：
     * text：文本类型。
     * file：文件类型。
     * image：图片类型。
     */
    private String type;

    /**
     * 文本内容。
     */
    private String text;

    /**
     * 文件或图片内容的 ID。
     * 必须是当前账号上传的文件 ID，上传方式可参考上传文件。
     * 在 type 为 file 或 image 时，file_id 和 file_url 应至少指定一个。
     */
    @JsonProperty("file_id")
    private String fileId;

    /**
     * 文件或图片内容的在线地址。必须是可公共访问的有效地址。
     * 在 type 为 file 或 image 时，file_id 和 file_url 应至少指定一个。
     */
    @JsonProperty("file_url")
    private String fileUrl;

}
