package com.luohuo.flex.model.enumeration.base;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.luohuo.basic.interfaces.BaseEnum;

import java.util.stream.Stream;

/**
 * <p>
 * 实体注释中生成的类型枚举
 * 角色
 * </p>
 *
 * @author zuihou
 * @date 2021-10-21
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "角色类别-枚举")
public enum RoleCategoryEnum implements BaseEnum {

    /**
     * 功能角色
     */
    FUNCTION("10", "功能角色"),
    /**
     * 桌面角色
     */
    DESKTOP("20", "桌面角色"),
    /**
     * 数据角色
     */
    DATA_SCOPE("30", "数据角色"),


    ;

    @Schema(description = "描述")
    private String code;

    private String desc;


    /**
     * 根据当前枚举的name匹配
     */
    public static RoleCategoryEnum match(String val, RoleCategoryEnum def) {
        return Stream.of(values()).parallel().filter(item -> item.name().equalsIgnoreCase(val)).findAny().orElse(def);
    }

    public static RoleCategoryEnum get(String val) {
        return match(val, null);
    }

    public boolean eq(RoleCategoryEnum val) {
        return val != null && eq(val.name());
    }

    @Override
    @Schema(description = "编码", allowableValues = "FUNCTION,DESKTOP,DATA_SCOPE", example = "FUNCTION")
    public String getCode() {
        return this.code;
    }

}
