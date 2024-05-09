package com.hula.core.user.domain.vo.req.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;



/**
 * 佩戴徽章
 * @author nyh
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WearingBadgeReq {

    @NotNull
    @Schema(description ="徽章id")
    private Long badgeId;

}
