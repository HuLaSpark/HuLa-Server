package com.luohuo.flex.oauth.vo.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import com.luohuo.basic.annotation.constraints.NotEmptyPattern;

import static com.luohuo.basic.utils.ValidatorUtil.REGEX_EMAIL;

/**
 * 登录参数
 *
 * @author 乾乾
 * @date 2020年01月05日22:18:12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Schema(description = "注册")
public class RegisterByEmailVO extends RegisterVO {

    /**
     * 邮箱
     */
    @Schema(description = "邮箱")
    @Size(max = 255, message = "邮箱长度不能超过{max}")
    @NotEmptyPattern(regexp = REGEX_EMAIL, message = "请输入正确的邮箱地址")
    @NotEmpty(message = "请填写登录邮箱")
    private String email;
}
