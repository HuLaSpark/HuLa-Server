package com.luohuo.flex.im.domain.vo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author nyh
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberResp implements Serializable {
	@Schema(description ="群聊id")
	private Long groupId;
    @Schema(description ="房间id")
    private Long roomId;
    @Schema(description ="群名称")
    private String groupName;
    @Schema(description ="群头像")
    private String avatar;
    @Schema(description ="在线人数")
    private Long onlineNum;
    @Schema(description ="成员角色 1群主 2管理员 3普通成员 4踢出群聊")
    private Integer roleId;
	@Schema(description = "群号")
	private String account;
	@Schema(description = "群成员数")
	private Long memberNum;
	@Schema(description = "群备注")
	private String remark;
	@Schema(description = "在这个群的昵称")
	private String myName;
	@Schema(description = "是否允许扫码直接进群")
	private Boolean allowScanEnter;
}
