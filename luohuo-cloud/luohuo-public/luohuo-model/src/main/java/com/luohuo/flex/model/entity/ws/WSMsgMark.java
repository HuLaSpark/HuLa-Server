package com.luohuo.flex.model.entity.ws;

import com.luohuo.flex.model.enums.MessageMarkTypeEnum;
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
        @Schema(description ="操作者")
        private String uid;
        @Schema(description ="消息id")
        private String msgId;
		/**
		 * @see MessageMarkTypeEnum
		 */
        private Integer markType;
        @Schema(description ="被标记的数量")
        private Integer markCount;
        @Schema(description ="动作类型 1确认 2取消")
        private Integer actType;
    }
}
