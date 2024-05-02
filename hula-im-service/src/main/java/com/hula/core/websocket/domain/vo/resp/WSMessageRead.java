package com.hula.core.websocket.domain.vo.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description:
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-03-19
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WSMessageRead {
    @Schema(description = "消息")
    private Long msgId;
    @Schema(description = "阅读人数（可能为0）")
    private Integer readCount;
}
