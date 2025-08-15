package com.luohuo.flex.im.domain.vo.req.user;

import com.luohuo.basic.base.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotNull;



/**
 * 修改用户名
 * @author nyh
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ModifyNameReq extends BaseEntity {

    @NotNull
    @Length(max = 8, message = "用户名可别取太长，不然我记不住噢")
    @Schema(description = "用户名")
    private String name;

}
