package com.hula.core.user.controller;

import com.hula.core.user.domain.vo.req.oss.UploadUrlReq;
import com.hula.core.user.service.OssService;
import com.hula.domain.vo.res.ApiResult;
import com.hula.domain.vo.res.OssResp;
import com.hula.utils.RequestHolder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * oss控制层
 * @author nyh
 */
@RestController
@RequestMapping("/api/oss")
@Api(tags = "oss相关接口")
public class OssController {
    @Resource
    private OssService ossService;

    @GetMapping("/upload/url")
    @ApiOperation("获取临时上传链接")
    public ApiResult<OssResp> getUploadUrl(@Valid UploadUrlReq req) {
        return ApiResult.success(ossService.getUploadUrl(RequestHolder.get().getUid(), req));
    }
}
