package com.luohuo.flex.im.domain.vo.resp.room;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 群成员简化响应
 * @author nyh
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupMemberSimpleResp {

    @Schema(description = "用户UID")
    private String uid;

    @Schema(description = "用户昵称")
    private String name;

    @Schema(description = "角色 1-群主 2-管理员 3-普通成员 4-已被移出")
    private Integer roleId;

    @Schema(description = "在线状态 1-在线 2-离线")
    private Integer activeStatus;

    @Schema(description = "IP归属地")
    private String locPlace;

    @Schema(description = "IP地址")
    private String ipAddress;
}
