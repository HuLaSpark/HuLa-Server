package com.luohuo.flex.model.entity.ws;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author nyh
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WSMemberChange implements Serializable {
    public static final Integer CHANGE_TYPE_ADD = 1;
    public static final Integer CHANGE_TYPE_REMOVE = 2;
	public static final Integer CHANGE_TYPE_QUIT = 3;
    @Schema(description ="群组id")
    private String roomId;
	@Schema(description ="变动类型 1加入群组 2移除群组 3退出群聊")
	private Integer changeType;
    @Schema(description ="变动uid集合")
    private List<ChatMember> userList;
	@Schema(description ="群内总人数")
	private Integer totalNum;
	@Schema(description ="在线总人数")
	private Integer onlineNum;
}
