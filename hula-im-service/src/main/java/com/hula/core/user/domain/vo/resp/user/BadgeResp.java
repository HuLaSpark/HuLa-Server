package com.hula.core.user.domain.vo.resp.user;

import com.hula.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


/**
 * 徽章信息
 * @author nyh
 */
@Data
@Schema(description ="徽章信息")
public class BadgeResp extends BaseEntity {

    @Schema(description ="徽章id")
    private Long id;

    @Schema(description ="徽章图标")
    private String img;

    @Schema(description ="徽章描述")
    private String describe;

    @Schema(description ="是否拥有 0否 1是")
    private Integer obtain;

    @Schema(description ="是否佩戴  0否 1是")
    private Integer wearing;
}
