package com.luohuo.flex.file.vo.param;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import com.luohuo.flex.file.enumeration.FileStorageType;

import java.io.Serializable;

/**
 * 附件上传
 *
 * @author 乾乾
 * @date 2019-05-12 18:49
 */
@Data
@Schema(description = "附件上传")
public class FileUploadVO implements Serializable {

    @Schema(description = "业务类型")
    @NotBlank(message = "请填写业务类型")
    private String bizType;

    @Schema(description = "桶")
    private String bucket;

    @Schema(description = "存储类型")
    private FileStorageType storageType;
}
