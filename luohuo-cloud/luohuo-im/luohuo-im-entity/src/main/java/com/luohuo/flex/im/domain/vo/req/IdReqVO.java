package com.luohuo.flex.im.domain.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author nyh
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IdReqVO implements Serializable {
	@Schema(description = "id")
    @NotNull
    private long id;
}
