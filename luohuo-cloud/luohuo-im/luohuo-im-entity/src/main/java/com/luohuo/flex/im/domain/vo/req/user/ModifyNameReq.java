package com.luohuo.flex.im.domain.vo.req.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

/**
 * 修改用户名
 * @author nyh
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ModifyNameReq implements Serializable {

	@Schema(description = "性别")
	private Integer sex;

	@Schema(description = "手机号")
	private String phone;

	@NotEmpty
	@Schema(description = "头像url")
	private String avatar;

    @NotNull
    @Length(max = 8, message = "用户名可别取太长，不然我记不住噢")
    @Schema(description = "用户名")
    private String name;

	@Schema(description = "个人简介")
	private String resume;
}
