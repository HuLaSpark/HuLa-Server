package com.hula.core.chat.domain.vo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 查询群聊 返回
 * @author mint
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomGroupResp {
    @Schema(description ="id")
    private Long id;
    @Schema(description ="房间id")
    private Long roomId;
    @Schema(description ="群名称")
    private String groupName;
    @Schema(description ="群头像")
    private String avatar;
}
