package com.luohuo.flex.im.controller.admin;

import com.luohuo.basic.base.R;
import com.luohuo.flex.im.core.admin.service.AdminStatsService;
import com.luohuo.flex.im.domain.vo.resp.admin.AdminStatsResp;
import com.luohuo.flex.im.domain.vo.resp.admin.LoginRankResp;
import com.luohuo.flex.im.domain.vo.resp.admin.ActiveUserResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

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

    @GetMapping("/login-rank")
    @Operation(summary = "用户登录次数排行榜")
    public R<List<LoginRankResp>> getLoginRank(
            @RequestParam(value = "start", required = false) LocalDateTime start,
            @RequestParam(value = "end", required = false) LocalDateTime end,
            @RequestParam(value = "rangeDays", required = false) Integer rangeDays,
            @RequestParam(value = "limit", required = false) Integer limit
    ) {
        LocalDateTime now = LocalDateTime.now();
        if (start == null || end == null) {
            int days = rangeDays != null ? rangeDays : 30;
            start = now.minusDays(days).with(LocalTime.MIN);
            end = now.with(LocalTime.MAX);
        }
        if (limit == null) limit = 50;
        return R.success(adminStatsService.getLoginRank(start, end, limit));
    }

    @GetMapping("/active-users")
    @Operation(summary = "活跃用户列表（按时间范围）")
    public R<List<ActiveUserResp>> getActiveUsers(
            @RequestParam(value = "start", required = false) LocalDateTime start,
            @RequestParam(value = "end", required = false) LocalDateTime end,
            @RequestParam(value = "rangeDays", required = false) Integer rangeDays,
            @RequestParam(value = "limit", required = false) Integer limit
    ) {
        LocalDateTime now = LocalDateTime.now();
        if (start == null || end == null) {
            int days = rangeDays != null ? rangeDays : 30;
            start = now.minusDays(days).with(LocalTime.MIN);
            end = now.with(LocalTime.MAX);
        }
        if (limit == null) limit = 200;
        return R.success(adminStatsService.getActiveUsers(start, end, limit));
    }
}
