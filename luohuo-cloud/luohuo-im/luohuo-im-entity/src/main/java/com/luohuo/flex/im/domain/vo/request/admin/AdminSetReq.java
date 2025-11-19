package com.luohuo.flex.im.domain.vo.request.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 添加管理员请求信息
 * @author nyh
 */
@Data
public class AdminSetReq {
    @NotNull
    @Schema(description ="房间号")
    private Long roomId;

    @NotNull
    @Size(min = 1, max = 3)
    @Schema(description ="需要添加管理的列表")
    private List<Long> uidList;
}
