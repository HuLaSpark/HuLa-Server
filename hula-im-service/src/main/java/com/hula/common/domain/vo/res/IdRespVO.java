package com.hula.common.domain.vo.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author nyh
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IdRespVO {
    @ApiModelProperty("id")
    private long id;
}
