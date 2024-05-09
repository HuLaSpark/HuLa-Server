package com.hula.core.chat.domain.vo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 群成员列表的成员信息
 * @author nyh
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMemberListResp {
    @Schema(description ="uid")
    private Long uid;
    @Schema(description ="用户名称")
    private String name;
    @Schema(description ="头像")
    private String avatar;
}
