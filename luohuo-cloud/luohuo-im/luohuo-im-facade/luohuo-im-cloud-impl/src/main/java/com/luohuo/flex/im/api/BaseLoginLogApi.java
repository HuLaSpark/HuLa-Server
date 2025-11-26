package com.luohuo.flex.im.api;

import com.luohuo.basic.base.R;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

@FeignClient(name = "${luohuo.feign.base-server:luohuo-base-server}", path = "/baseLoginLog")
public interface BaseLoginLogApi {

    @GetMapping("/stats/login-rank")
    @Operation(summary = "登录次数排行榜（status=01）")
    R<List<LoginRankDTO>> getLoginRank(@RequestParam("start") LocalDateTime start,
                                       @RequestParam("end") LocalDateTime end,
                                       @RequestParam("limit") Integer limit);

    @GetMapping("/stats/user-count")
    @Operation(summary = "登录次数达到阈值的用户数量（status=01）")
    R<Long> countUsersWithMinLogins(@RequestParam("start") LocalDateTime start,
                                    @RequestParam("end") LocalDateTime end,
                                    @RequestParam("minTimes") Integer minTimes);

    class LoginRankDTO {
        public Long uid;
        public Long total;
    }
}