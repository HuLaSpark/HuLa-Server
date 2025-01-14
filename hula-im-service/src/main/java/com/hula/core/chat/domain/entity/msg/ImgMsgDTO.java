package com.hula.core.chat.domain.entity.msg;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;

/**
 * 图片消息入参
 * @author nyh
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ImgMsgDTO extends BaseFileDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description ="宽度（像素）")
//    @NotNull
    private Integer width;

    @Schema(description ="高度（像素）")
//    @NotNull
    private Integer height;

}


