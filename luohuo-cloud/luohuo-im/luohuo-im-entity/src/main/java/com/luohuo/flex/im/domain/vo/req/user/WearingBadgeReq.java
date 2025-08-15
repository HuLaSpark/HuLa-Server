package com.luohuo.flex.im.domain.vo.req.user;

import com.luohuo.basic.base.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 佩戴徽章
 * @author nyh
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WearingBadgeReq extends BaseEntity {

    @NotNull
    @Schema(description ="徽章id")
    private Long badgeId;

}
