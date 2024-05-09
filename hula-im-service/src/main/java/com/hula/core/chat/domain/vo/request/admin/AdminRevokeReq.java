package com.hula.core.chat.domain.vo.request.admin;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 撤销管理员请求信息
 * @author nyh
 */
@Data
public class AdminRevokeReq {
    @NotNull
    @Schema(description ="房间号")
    private Long roomId;

    @NotNull
    @Size(min = 1, max = 3)
    @Schema(description ="需要撤销管理的列表")
    private List<Long> uidList;
}
