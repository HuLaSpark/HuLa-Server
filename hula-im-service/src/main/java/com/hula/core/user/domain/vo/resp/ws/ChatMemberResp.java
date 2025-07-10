package com.hula.core.user.domain.vo.resp.ws;

import com.hula.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 成员列表的成员信息
 *
 * @author nyh
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMemberResp extends BaseEntity {
    private Long id;

    @Schema(description = "uid")
    private String uid;

    @Schema(description = "HuLa号")
	private String account;

    @Schema(description = "在线状态 1在线 2离线")
    private Integer activeStatus;

    @Schema(description = "角色ID")
    private Integer roleId;

	@Schema(description = "IP归属地")
	private String locPlace;

    @Schema(description = "最后一次上下线时间")
    private Date lastOptTime;

    @Schema(description = "我的群昵称")
    private String myName;

    @Schema(description = "群角色")
    private Integer groupRole;

}
