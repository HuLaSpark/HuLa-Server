package com.luohuo.flex.im.domain.vo.req.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 编辑黑名单请求
 * @author nyh
 */
@Data
public class BlackEditReq implements Serializable {

    @NotNull(message = "黑名单ID不能为空")
    @Schema(description = "黑名单ID")
    private Long id;

    @NotNull(message = "请选择拉黑类型")
    @Schema(description = "拉黑类型 1.IP 2.UID")
    private Integer type;

    @NotNull(message = "请输入拉黑目标")
    @Schema(description = "拉黑目标（IP或UID）")
    private String target;

    @Schema(description = "截止时间（yyyy-MM-dd HH:mm:ss格式），空字符串表示永久")
    private String deadline;
}
