package com.luohuo.flex.im.domain.vo.req.room;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 解散群聊请求
 * @author nyh
 */
@Data
public class DisbandGroupReq implements Serializable {

    @NotNull(message = "房间ID不能为空")
    @Schema(description = "房间ID")
    private Long roomId;
}
