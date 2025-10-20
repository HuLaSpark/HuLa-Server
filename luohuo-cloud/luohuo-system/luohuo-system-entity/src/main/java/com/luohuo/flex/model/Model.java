package com.luohuo.flex.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.luohuo.basic.base.entity.SuperEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统模型表实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_model")
public class Model extends SuperEntity<Long> {

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