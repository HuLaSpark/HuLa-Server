package com.hula.core.websocket.domain.vo.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


/**
 * @author nyh
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WSMsgMark {
    private List<WSMsgMarkItem> markList;

    @Data
    public static class WSMsgMarkItem {
        @Schema(description = "操作者")
        private Long uid;
        @Schema(description = "消息id")
        private Long msgId;
        @Schema(description = "标记类型 1点赞 2举报")
        private Integer markType;
        @Schema(description = "被标记的数量")
        private Integer markCount;
        @Schema(description = "动作类型 1确认 2取消")
        private Integer actType;
    }
}
