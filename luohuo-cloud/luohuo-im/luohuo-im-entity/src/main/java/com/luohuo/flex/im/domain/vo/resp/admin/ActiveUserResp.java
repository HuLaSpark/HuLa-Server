package com.luohuo.flex.im.domain.vo.resp.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "活跃用户")
public class ActiveUserResp {
    private String username;
    private String nickName;
    private String avatar;
    private LocalDateTime lastOptTime;
    private String ip;
    private String location;
    private String isp;
    private Integer loginTimes;
}