package com.luohuo.flex.im.controller.user;

import com.luohuo.basic.base.R;
import com.luohuo.flex.im.core.user.service.PrivacyService;
import com.luohuo.flex.im.domain.dto.PrivacySettingReq;
import com.luohuo.flex.im.domain.entity.UserPrivacy;
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
    public R<UserPrivacy> getPrivacySetting(@RequestParam("uid") Long uid) {
        return R.success(privacyService.getPrivacySetting(uid));
    }
    
    /**
     * 更新隐私设置
     */
    @PostMapping
    public R<Boolean> updatePrivacySetting(@RequestParam("uid") Long uid, @RequestBody PrivacySettingReq req) {
        return R.success(privacyService.updatePrivacySetting(uid, req));
    }
}