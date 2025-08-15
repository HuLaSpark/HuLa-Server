package com.luohuo.flex.model.enumeration.system;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.luohuo.basic.interfaces.BaseEnum;

import java.util.stream.Stream;

/**
 * <p>
 * 租户状态枚举
 * </p>
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "状态-枚举")
public enum DefTenantStatusEnum implements BaseEnum {

    /**
     * NORMAL="正常"
     */
    NORMAL("0", "正常"),
	/**
	 * WAITING="审核中"
	 */
	WAITING("1", "审核中"),
    /**
     * REFUSE="停用"
     */
    REFUSE("2", "停用"),
	/**
	 * WAIT_INIT_DATASOURCE="待初始化租户"
	 */
	WAIT_INIT_DATASOURCE("3", "待初始化租户"),
    ;

    @Schema(description = "描述")
    private String code;
    @Schema(description = "描述")
    private String desc;

    /**
     * 根据当前枚举的name匹配
     */
    public static DefTenantStatusEnum match(String val, DefTenantStatusEnum def) {
        return Stream.of(values()).parallel().filter(item -> item.name().equalsIgnoreCase(val)).findAny().orElse(def);
    }

    public static DefTenantStatusEnum get(String val) {
        return match(val, null);
    }

    public boolean eq(DefTenantStatusEnum val) {
        return val != null && eq(val.name());
    }

    @Override
    @Schema(description = "编码")
    public String getCode() {
        return this.code;
    }

}
