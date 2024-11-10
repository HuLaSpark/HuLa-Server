package com.hula.core.chat.domain.entity.msg;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * 视频消息入参
 * @author nyh
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class VideoMsgDTO extends BaseFileDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description ="缩略图宽度（像素）")
    @NotNull
    private Integer thumbWidth;

    @Schema(description ="缩略图高度（像素）")
    @NotNull
    private Integer thumbHeight;

    @Schema(description ="缩略图大小（字节）")
    @NotNull
    private Long thumbSize;

    @Schema(description ="缩略图下载地址")
    @NotBlank
    private String thumbUrl;

}
