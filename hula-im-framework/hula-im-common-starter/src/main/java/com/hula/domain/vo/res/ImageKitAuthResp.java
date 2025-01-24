package com.hula.domain.vo.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "ImageKit认证响应")
public class ImageKitAuthResp {
    @Schema(description = "ImageKit认证参数")
    private Map<String, String> authParams;
}