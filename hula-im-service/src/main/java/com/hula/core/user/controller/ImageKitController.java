package com.hula.core.user.controller;

import com.hula.domain.vo.res.ApiResult;
import com.hula.domain.vo.res.ImageKitAuthResp;
import io.imagekit.sdk.ImageKit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * imagekit控制层
 * @author nyh
 */
@RestController
@RequestMapping("/imagekit")
@Api(tags = "imagekit认证接口")
public class ImageKitController {

    @Resource
    private ImageKit imageKit;

    @GetMapping("/auth")
    @ApiOperation("获取ImageKit认证参数")
    public ApiResult<ImageKitAuthResp> getImageKitAuth() {
        Map<String, String> authParams = imageKit.getAuthenticationParameters();
        return ApiResult.success(ImageKitAuthResp.builder()
                .authParams(authParams)
                .build());
    }
}
