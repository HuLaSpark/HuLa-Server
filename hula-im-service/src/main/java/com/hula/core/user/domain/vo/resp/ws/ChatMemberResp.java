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

    @Schema(description = "uid")
    private Long uid;

    @Schema(description = "HuLa号")
    private String accountCode;

    @Schema(description = "在线状态 1在线 2离线")
    private Integer activeStatus;

    @Schema(description = "角色ID")
    private Integer roleId;

	@Schema(description = "IP归属地")
	private String locPlace;

    @Schema(description = "最后一次上下线时间")
    private Date lastOptTime;

}
