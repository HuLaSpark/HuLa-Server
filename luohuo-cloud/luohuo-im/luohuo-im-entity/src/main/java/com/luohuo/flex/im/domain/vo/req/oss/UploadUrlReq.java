package com.luohuo.flex.im.domain.vo.req.oss;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 上传url请求入参
 * @author nyh
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UploadUrlReq implements Serializable {
	@Schema(description = "文件名（带后缀）")
    @NotBlank
    private String fileName;

	@Schema(description = "上传场景chat.聊天室,emoji.表情包,avatar.头像")
    @NotNull
    private String scene;
}
