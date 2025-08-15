package com.luohuo.flex.im.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 房间详情
 * @author nyh
 */
@Data
public class RoomBaseInfo {
	@Schema(description ="好友id | 群id")
	private Long id;

    @Schema(description ="房间id")
    private Long roomId;

	@Schema(description ="Hula账号、群号")
	private String account;

    @Schema(description ="会话名称")
    private String name;

    @Schema(description ="会话头像")
    private String avatar;

	@Schema(description ="房间类型 1群聊 2单聊")
    private Integer type;

	@Schema(description ="是否全员展示 0否 1是")
    private Integer hotFlag;

	@Schema(description ="群最后消息的更新时间")
    private LocalDateTime activeTime;

	@Schema(description ="最后一条消息id")
    private Long lastMsgId;

	@Schema(description ="我在群里的角色")
	private Integer roleId;

	@Schema(description ="群备注")
	private String remark;

	@Schema(description ="我的群名称")
	private String myName;
}
