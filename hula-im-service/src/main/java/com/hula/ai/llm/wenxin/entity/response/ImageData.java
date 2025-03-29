package com.hula.ai.llm.wenxin.entity.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 文本生成图片内容
 *
 * @author: 云裂痕
 * @date: 2024/1/6
 * @version: 1.1.1
 * 得其道
 * 乾乾
 */
@Data
public class ImageData implements Serializable {

    /**
     * 固定值"image"
     */
    private String object;

    /**
     * 图片base64编码内容
     */
    @JsonProperty("b64_image")
    private String b64Image;

    /**
     * 序号
     */
    private Integer index;

}
