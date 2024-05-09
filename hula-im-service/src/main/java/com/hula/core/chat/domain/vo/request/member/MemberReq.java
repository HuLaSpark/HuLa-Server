package com.hula.core.chat.domain.vo.request.member;

import com.hula.common.domain.vo.req.CursorPageBaseReq;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @author nyh
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberReq extends CursorPageBaseReq {
    @Schema(description ="房间号")
    private Long roomId = 1L;
}
