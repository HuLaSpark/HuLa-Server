package com.hula.core.user.controller;

import com.alibaba.fastjson.JSONObject;
import com.hula.common.storage.StorageDriver;
import com.hula.core.user.domain.vo.req.oss.UploadUrlReq;
import com.hula.core.user.domain.vo.resp.config.Init;
import com.hula.core.user.service.ConfigService;
import com.hula.core.user.service.OssService;
import com.hula.domain.vo.res.ApiResult;
import com.hula.domain.vo.res.OssResp;
import com.hula.utils.RequestHolder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统控制层
 * @author nyh
 */
@RestController
@RequestMapping("system/")
@Api(tags = "oss相关接口")
public class RootController {
    @Resource
    private OssService ossService;

	@Resource
	private StorageDriver storageDriver;

	@Resource
	private ConfigService configService;

    @GetMapping("oss/upload/url")
    @ApiOperation("获取MinIO临时上传链接")
    public ApiResult<OssResp> getUploadUrl(@Valid UploadUrlReq req) {
        return ApiResult.success(ossService.getUploadUrl(RequestHolder.get().getUid(), req));
    }

	@Operation(summary = "获取七牛云上传token")
	@GetMapping("ossToken")
	public ApiResult<JSONObject> token() {
		return ApiResult.success(storageDriver.getToken());
	}


	@GetMapping("config/init")
	@ApiOperation("获取系统全局配置")
	public ApiResult<Init> init() {
		return ApiResult.success(configService.getSystemInit());
	}
}