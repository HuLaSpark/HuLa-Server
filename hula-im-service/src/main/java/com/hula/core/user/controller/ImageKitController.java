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

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
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
        
        // 获取1小时后的UTC时间戳
        long expireTimestamp = ZonedDateTime.now(ZoneId.of("UTC"))
                .plusHours(1)
                .toInstant()
                .getEpochSecond();
                
        // 创建新的参数Map，确保使用正确的时间戳
        Map<String, String> updatedParams = new HashMap<>(authParams);
        updatedParams.put("expire", String.valueOf(expireTimestamp));
        
        return ApiResult.success(ImageKitAuthResp.builder()
                .authParams(updatedParams)
                .build());
    }
}
