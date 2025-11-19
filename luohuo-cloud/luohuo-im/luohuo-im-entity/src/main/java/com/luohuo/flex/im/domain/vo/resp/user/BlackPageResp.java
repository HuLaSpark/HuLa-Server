package com.luohuo.flex.im.domain.vo.resp.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 黑名单分页响应
 * @author nyh
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BlackPageResp implements Serializable {

    @Schema(description = "黑名单ID")
    private Long id;

    @Schema(description = "拉黑目标类型 1.ip 2.uid")
    private Integer type;

    @Schema(description = "拉黑目标")
    private String target;

    @Schema(description = "截止时间")
    private LocalDateTime deadline;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "用户昵称（仅当type=2时有值）")
    private String userName;
}
