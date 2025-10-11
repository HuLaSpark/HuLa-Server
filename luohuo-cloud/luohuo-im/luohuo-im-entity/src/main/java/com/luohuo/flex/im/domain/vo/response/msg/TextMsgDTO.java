package com.luohuo.flex.im.domain.vo.response.msg;

import com.luohuo.flex.im.domain.UrlInfo;
import com.luohuo.flex.im.domain.entity.msg.ReplyMsg;
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
public class TextMsgDTO {
    @Schema(description ="消息内容")
    private String content;
    @Schema(description ="消息链接映射")
    private Map<String, UrlInfo> urlContentMap;
    @Schema(description ="艾特的uid")
    private List<Long> atUidList;
    @Schema(description ="父消息，如果没有父消息，返回的是null")
    private ReplyMsg reply;
}
