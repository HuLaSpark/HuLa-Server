package com.luohuo.flex.model.entity.ws;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 消息
 * @author nyh
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageResp implements Serializable {

    @Schema(description ="发送者信息")
    private UserInfo fromUser;
    @Schema(description ="消息详情")
    private Message message;

    @Data
    public static class UserInfo {
        @Schema(description ="用户id")
        private String uid;
    }

    @Data
    public static class Message {
        @Schema(description ="消息id")
        private String id;
        @Schema(description ="房间id")
        private String roomId;
        @Schema(description ="消息发送时间")
        private LocalDateTime sendTime;
        @Schema(description ="消息类型 1正常文本 2.撤回消息")
        private Integer type;
        @Schema(description ="消息内容不同的消息类型，内容体不同")
        private Object body;
		@Schema(description = "扩展标记统计（type为MessageMarkTypeEnum的type字段）")
		private Map<Integer, MarkItem> messageMarks;
    }

	@Data
	@AllArgsConstructor
	public static class MarkItem {
		@Schema(description = "标记数量")
		private Integer count;
		@Schema(description = "当前用户是否标记")
		private Boolean userMarked;
	}
}
