package com.hula.core.chat.domain.vo.response.msg;

import com.hula.common.utils.discover.domain.UrlInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 文本消息返回体
 * @author nyh
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TextMsgResp {
    @Schema(description ="消息内容")
    private String content;
    @Schema(description ="消息链接映射")
    private Map<String, UrlInfo> urlContentMap;
    @Schema(description ="艾特的uid")
    private List<Long> atUidList;
    @Schema(description ="父消息，如果没有父消息，返回的是null")
    private ReplyMsg reply;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ReplyMsg {
        @Schema(description ="消息id")
        private Long id;
        @Schema(description ="用户uid")
        private Long uid;
        @Schema(description ="用户名称")
        private String username;
        @Schema(description ="消息类型 1正常文本 2.撤回消息")
        private Integer type;
        @Schema(description ="消息内容不同的消息类型，见父消息内容体")
        private Object body;
        @Schema(description ="是否可消息跳转 0否 1是")
        private Integer canCallback;
        @Schema(description ="跳转间隔的消息条数")
        private Integer gapCount;
    }
}
