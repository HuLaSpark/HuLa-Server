package com.luohuo.flex.im.controller.admin;

import com.luohuo.basic.base.R;
import com.luohuo.flex.im.core.admin.service.AdminStatsService;
import com.luohuo.flex.im.domain.vo.resp.admin.AdminStatsResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 后台管理统计接口
 * @author 乾乾
 */
@RestController
@RequestMapping("/admin/stats")
@Tag(name = "后台管理统计接口")
@Slf4j
public class AdminStatsController {

    @Resource
    private AdminStatsService adminStatsService;

    @GetMapping("/home")
    @Operation(summary = "首页统计数据")
    public R<AdminStatsResp> getHomeStats() {
        return R.success(adminStatsService.getHomeStats());
    }
}
