package com.luohuo.flex.im.domain.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "标记通知已读请求")
public class NoticeReadReq {
    
    @NotNull
    @Schema(description = "通知ID", required = true)
    private Long noticeId;
}