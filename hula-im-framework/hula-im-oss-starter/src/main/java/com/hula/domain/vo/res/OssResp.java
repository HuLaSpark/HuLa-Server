package com.hula.domain.vo.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 上传url请求出参
 * @author nyh
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OssResp {

    @Schema(description ="上传的临时url")
    private String uploadUrl;

    @Schema(description ="成功后能够下载的url")
    private String downloadUrl;
}
