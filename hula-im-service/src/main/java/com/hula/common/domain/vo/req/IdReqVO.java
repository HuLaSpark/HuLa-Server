package com.hula.common.domain.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;

/**
 * @author nyh
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IdReqVO {
    @ApiModelProperty("id")
    @NotNull
    private long id;
}
