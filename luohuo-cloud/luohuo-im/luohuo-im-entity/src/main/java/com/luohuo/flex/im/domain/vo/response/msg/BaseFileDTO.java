package com.luohuo.flex.im.domain.vo.response.msg;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;


/**
 * 文件基类
 * @author nyh
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class BaseFileDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Schema(description ="大小（字节）")
    @NotNull
    private Long size;

    @Schema(description ="下载地址")
    @NotBlank
    private String url;
}
