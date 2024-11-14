package com.hula.core.user.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 修改用户名
 * @author nyh
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemInfoDTO {

    @ApiModelProperty(value = "徽章id")
    private Long itemId;

    @ApiModelProperty(value = "是否需要刷新")
    private Boolean needRefresh = Boolean.TRUE;

    @ApiModelProperty("徽章图像")
    private String img;

    @ApiModelProperty("徽章说明")
    private String describe;

    public static ItemInfoDTO skip(Long itemId) {
        ItemInfoDTO dto = new ItemInfoDTO();
        dto.setItemId(itemId);
        dto.setNeedRefresh(Boolean.FALSE);
        return dto;
    }
}
