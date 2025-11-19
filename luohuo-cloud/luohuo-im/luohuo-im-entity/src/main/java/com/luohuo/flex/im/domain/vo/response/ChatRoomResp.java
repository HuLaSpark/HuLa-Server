package com.luohuo.flex.im.domain.vo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 群成员列表的成员信息
 * @author nyh
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomResp implements Serializable {
	@Schema(description ="会话id")
	private Long id;
	@Schema(description ="单聊时对方的id，群聊是groupId")
	private Long detailId;
    @Schema(description ="房间id")
    private Long roomId;
    @Schema(description ="房间类型 1群聊 2单聊")
    private Integer type;
    @Schema(description ="是否全员展示的会话 0否 1是")
    private Integer hotFlag;
    @Schema(description ="最新消息")
    private String text;
    @Schema(description ="会话名称")
    private String name;
    @Schema(description ="会话头像")
    private String avatar;
    @Schema(description ="房间最后活跃时间(用来排序)")
    private LocalDateTime activeTime;
    @Schema(description ="未读数")
    private Integer unreadCount;
	@Schema(description ="Hula账号、群号")
	private String account;
	@Schema(description ="是否置顶")
	private Boolean top;
	@Schema(description ="删除会话")
	private Boolean hide;
	@Schema(description="true->屏蔽 false -> 正常")
	private Boolean shield;
	@Schema(description ="0 -> 删除好友 1 -> 解散群聊 2,3 -> 退出该群")
	private Integer operate;
	@Schema(description = "通知类型 0 -> 允许接受消息 1 -> 接收但不提醒[免打扰] 4 -> 已退出群聊")
	private Integer muteNotification;
	@Schema(description ="群备注 [群专属]")
	private String remark;
	@Schema(description ="我的群名称 [群专属]")
	private String myName;
}
