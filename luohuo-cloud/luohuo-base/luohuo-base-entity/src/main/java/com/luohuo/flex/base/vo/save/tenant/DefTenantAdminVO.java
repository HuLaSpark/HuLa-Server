package com.luohuo.flex.base.vo.save.tenant;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@Schema(title = "DefTenantAdminVO", description = "绑定企业管理员")
public class DefTenantAdminVO {

    @Schema(description = "租户id")
    private Long tenantId;

    @Schema(description = "用户id列表")
	@NotEmpty(message = "请选择用户")
    private List<Long> userIdList;

    private Boolean isBind;

    private String reviewComments;
}
