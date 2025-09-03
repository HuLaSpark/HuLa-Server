package com.luohuo.flex.im.domain.vo.resp.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;


/**
 * 徽章信息
 * @author nyh
 */
@Data
@Schema(description ="徽章信息")
public class BadgeResp implements Serializable {

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
