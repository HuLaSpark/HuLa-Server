package com.luohuo.flex.base.vo.save.application;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 实体类
 * 用户的默认应用
 * </p>
 *
 * @author zuihou
 * @since 2022-03-06
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@Schema(description = "用户的默认应用")
public class DefUserApplicationSaveVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 所属用户ID
     */
    @Schema(description = "所属用户ID")
    @NotNull(message = "请填写所属用户ID")
    private Long userId;
    /**
     * 所属应用ID
     */
    @Schema(description = "所属应用ID")
    @NotNull(message = "请填写所属应用ID")
    private Long applicationId;

}
