package com.hula.core.user.controller;

import com.hula.core.chat.domain.entity.UserPrivacy;
import com.hula.core.user.domain.dto.PrivacySettingReq;
import com.hula.core.user.service.PrivacyService;
import com.hula.domain.vo.res.ApiResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/privacy")
@RequiredArgsConstructor
public class PrivacyController {
    
    private final PrivacyService privacyService;
    
    /**
     * 获取隐私设置
     */
    @GetMapping
    public ApiResult<UserPrivacy> getPrivacySetting(@RequestParam("uid") Long uid) {
        return ApiResult.success(privacyService.getPrivacySetting(uid));
    }
    
    /**
     * 更新隐私设置
     */
    @PostMapping
    public ApiResult<Boolean> updatePrivacySetting(@RequestParam("uid") Long uid, @RequestBody PrivacySettingReq req) {
        return ApiResult.success(privacyService.updatePrivacySetting(uid, req));
    }
}