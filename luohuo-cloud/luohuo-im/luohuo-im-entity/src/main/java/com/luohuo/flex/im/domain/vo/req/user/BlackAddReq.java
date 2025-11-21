package com.luohuo.flex.im.domain.vo.req.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 通用拉黑请求（支持IP和UID）
 * @author nyh
 */
@Data
@Schema(description = "通用拉黑请求")
public class BlackAddReq implements Serializable {

    @NotNull(message = "请选择拉黑类型")
    @Schema(description = "拉黑类型：1=IP，2=UID")
    private Integer type;

    @NotBlank(message = "请输入拉黑目标")
    @Schema(description = "拉黑目标（IP 或 UID）")
    private String target;

    @NotNull(message = "请输入截止时间")
    @Schema(description = "截止时间（分钟），0表示永久拉黑")
    private Long deadline;
}
