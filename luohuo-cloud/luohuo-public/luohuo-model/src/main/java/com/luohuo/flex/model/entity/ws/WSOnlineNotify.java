package com.luohuo.flex.model.entity.ws;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户上线好友变动的推送类
 * @author 乾乾
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WSOnlineNotify {
	@Schema(description = "新的上下线用户")
    private String uid;
	@Schema(description = "指纹")
	private String clientId;
	@Schema(description = "当前房间的在线人数信息")
	private String roomId;
	@Schema(description = "最后一次上下线时间")
	private Long lastOptTime;
	@Schema(description = "在线人数")
    private Long onlineNum;

	@Schema(description = "1 = 群聊 2 = 好友")
	private Integer type;

	public WSOnlineNotify(Long uid, String clientId, Long lastOptTime, Long onlineNum, Integer type) {
		this.uid = uid+"";
		this.clientId = clientId;
		this.lastOptTime = lastOptTime;
		this.onlineNum = onlineNum;
		this.type = type;
	}

	public WSOnlineNotify(Long roomId, Long uid, String clientId, Long lastOptTime, Long onlineNum, Integer type) {
		this.roomId = roomId+"";
		this.uid = uid+"";
		this.clientId = clientId;
		this.lastOptTime = lastOptTime;
		this.onlineNum = onlineNum;
		this.type = type;
	}
}
