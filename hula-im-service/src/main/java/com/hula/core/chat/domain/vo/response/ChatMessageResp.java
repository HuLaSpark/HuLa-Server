package com.hula.core.chat.domain.vo.response;

import com.hula.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 消息
 * @author nyh
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageResp extends BaseEntity {

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
        private Date sendTime;
        @Schema(description ="消息类型 1正常文本 2.撤回消息")
        private Integer type;
        @Schema(description ="消息内容不同的消息类型，内容体不同")
        private Object body;
        @Schema(description ="消息标记")
        private MessageMark messageMark;
    }

    @Data
    public static class MessageMark {
        @Schema(description ="点赞数")
        private Integer likeCount;
        @Schema(description ="该用户是否已经点赞 0否 1是")
        private Integer userLike;
        @Schema(description ="举报数")
        private Integer dislikeCount;
        @Schema(description ="该用户是否已经举报 0否 1是")
        private Integer userDislike;
    }
}
