package com.luohuo.flex.im.domain.vo.resp.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 后台管理首页统计数据响应
 * @author 乾乾
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "后台管理首页统计数据")
public class AdminStatsResp {

    @Schema(description = "今日活跃用户数")
    private Integer todayActiveUser;

    @Schema(description = "群聊总数")
    private Integer totalGroup;

    @Schema(description = "当前黑名单数")
    private Integer blackCount;

    @Schema(description = "今日 AI 调用次数")
    private Integer aiCallToday;

    @Schema(description = "黑名单统计")
    private BlackStats blackStats;

    @Schema(description = "AI 统计")
    private AiStats aiStats;

    @Schema(description = "最近一个月登录≥3次的用户总数")
    private Integer monthlyLogin3PlusUserCount;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "黑名单统计")
    public static class BlackStats {
        @Schema(description = "今日新增黑名单")
        private Integer todayNew;

        @Schema(description = "本周新增黑名单")
        private Integer weekNew;

        @Schema(description = "黑名单总数")
        private Integer total;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "AI 统计")
    public static class AiStats {
        @Schema(description = "今日调用次数")
        private Integer todayCalls;

        @Schema(description = "本周调用次数")
        private Integer weekCalls;

        @Schema(description = "活跃模型数")
        private Integer activeModels;
    }
}
