package com.luohuo.flex.model.enumeration.system;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
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
public enum DefTenantStatusEnum implements Serializable {

    /**
     * NORMAL="正常"
     */
    NORMAL(0, "正常"),
	/**
	 * WAITING="审核中"
	 */
	WAITING(1, "审核中"),
    /**
     * REFUSE="停用"
     */
    REFUSE(2, "停用"),
	/**
	 * WAIT_INIT_DATASOURCE="待初始化租户"
	 */
	WAIT_INIT_DATASOURCE(3, "待初始化租户"),
    ;

    @Schema(description = "code值")
    private Integer code;
    @Schema(description = "描述")
    private String desc;

    public Integer getCodeInt() {
        return this.code;
    }

	public Integer getCode() {
		return this.code;
	}

    /**
     * 根据当前枚举的name匹配
     */
    public static DefTenantStatusEnum match(String val, DefTenantStatusEnum def) {
        return Stream.of(values()).parallel().filter(item -> item.name().equalsIgnoreCase(val)).findAny().orElse(def);
    }

    public static DefTenantStatusEnum get(String val) {
        return match(val, null);
    }

    /**
     * 根据code匹配枚举
     */
    public static DefTenantStatusEnum matchByCode(Integer code, DefTenantStatusEnum def) {
        if (code == null) {
            return def;
        }
        return Stream.of(values()).parallel().filter(item -> item.getCodeInt().equals(code)).findAny().orElse(def);
    }

    public static DefTenantStatusEnum getByCode(Integer code) {
        return matchByCode(code, null);
    }

    /**
     * 判断是否等于指定的Integer值
     */
    public boolean eq(Integer val) {
        return val != null && this.code.equals(val);
    }

    /**
     * 判断是否等于指定的枚举值
     */
    public boolean eq(DefTenantStatusEnum val) {
        return val != null && this.code.equals(val.code);
    }

}
