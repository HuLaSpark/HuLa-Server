package com.luohuo.flex.im.domain.vo.request;

import com.luohuo.flex.im.domain.vo.req.CursorPageBaseReq;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;


/**
 * @author nyh
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageReadReq extends CursorPageBaseReq {
    @Schema(description ="消息id")
    @NotNull
    private Long msgId;

    @Schema(description ="查询类型 1已读 2未读")
    @NotNull
    private Long searchType;
}
