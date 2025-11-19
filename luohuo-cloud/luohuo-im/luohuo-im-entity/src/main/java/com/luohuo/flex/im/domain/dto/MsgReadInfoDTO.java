package com.luohuo.flex.im.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author nyh
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MsgReadInfoDTO {
    @Schema(description ="消息id")
    private Long msgId;

    @Schema(description ="已读数")
    private Integer readCount;

    @Schema(description ="未读数")
    private Integer unReadCount;

}
