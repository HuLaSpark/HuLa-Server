package com.luohuo.flex.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 模型信息
 *
 * @author 乾乾
 * @date 2025/10/20
 */
@Data
@Schema(description = "模型信息视图对象")
public class ModelVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "模型唯一键")
    private String modelKey;

    @Schema(description = "模型名称")
    private String modelName;

    @Schema(description = "模型文件URL")
    private String modelUrl;

    @Schema(description = "模型描述")
    private String description;

    @Schema(description = "模型版本号")
    private String version;

    @Schema(description = "状态：0-禁用，1-启用")
    private Boolean status;
}