package com.hula.common.websocket.domain.vo.resp;

import io.swagger.annotations.ApiModelProperty;
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
        @ApiModelProperty("操作者")
        private Long uid;
        @ApiModelProperty("消息id")
        private Long msgId;
        @ApiModelProperty("标记类型 1点赞 2举报")
        private Integer markType;
        @ApiModelProperty("被标记的数量")
        private Integer markCount;
        @ApiModelProperty("动作类型 1确认 2取消")
        private Integer actType;
    }
}
