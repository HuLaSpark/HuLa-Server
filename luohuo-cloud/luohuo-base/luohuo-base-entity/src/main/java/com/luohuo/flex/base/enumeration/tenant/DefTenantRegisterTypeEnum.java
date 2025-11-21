package com.luohuo.flex.base.enumeration.tenant;

import com.luohuo.basic.interfaces.BaseEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.stream.Stream;

/**
 * <p>
 * 实体注释中生成的类型枚举
 * 企业
 * </p>
 *
 * @author 乾乾
 * @date 2021-10-27
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "DefTenantRegisterTypeEnum", description = "类型-枚举")
public enum DefTenantRegisterTypeEnum implements BaseEnum {

    /**
     * CREATE="创建"
     */
    CREATE("创建"),
    /**
     * REGISTER="注册"
     */
    REGISTER("注册");

    @Schema(description = "描述")
    private String desc;


    /**
     * 根据当前枚举的name匹配
     */
    public static DefTenantRegisterTypeEnum match(String val, DefTenantRegisterTypeEnum def) {
        return Stream.of(values()).parallel().filter(item -> item.name().equalsIgnoreCase(val)).findAny().orElse(def);
    }

    public static DefTenantRegisterTypeEnum get(String val) {
        return match(val, null);
    }

    public boolean eq(DefTenantRegisterTypeEnum val) {
        return val != null && eq(val.name());
    }

    @Override
    @Schema(description = "编码", allowableValues = "CREATE,REGISTER", example = "CREATE")
    public String getCode() {
        return this.name();
    }

}
