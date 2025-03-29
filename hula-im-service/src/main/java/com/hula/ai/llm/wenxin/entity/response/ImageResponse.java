package com.hula.ai.llm.wenxin.entity.response;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 文本生成图片返回
 *
 * @author: 云裂痕
 * @date: 2024/01/06
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
@Data
@ToString
public class ImageResponse implements Serializable {

    private String id;

    private String object;

    private Long created;

    /**
     * 生成图片结果
     */
    private List<ImageData> data;

    /**
     * 使用量
     */
    private Usage usage;

}
