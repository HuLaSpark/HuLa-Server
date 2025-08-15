package com.luohuo.flex.oauth.enumeration.system;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.luohuo.basic.interfaces.BaseEnum;

import java.util.stream.Stream;

/**
 * @author zuihou
 * @date 2021/11/12 9:06
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "客户端类型-枚举")
public enum ClientTypeEnum implements BaseEnum {
    /**
     * 登录成功
     */
	LUOHUO_WEB("01", "开源版"),
	LUOHUO_WEB_PRO_SOYBEAN("03", "基于soybean前端"),
	LUOHUO_WEB_PRO_VBEN5("04", "基于vben5的前端"),

    ;

    @Schema(description = "code")
    private String code;
    @Schema(description = "描述")
    private String desc;


    /**
     * 根据当前枚举的name匹配
     */
    public static ClientTypeEnum match(String val, ClientTypeEnum def) {
        return Stream.of(values()).parallel().filter(item -> item.name().equalsIgnoreCase(val)).findAny().orElse(def);
    }

    public static ClientTypeEnum get(String val) {
        return match(val, null);
    }

    public boolean eq(ClientTypeEnum val) {
        return val != null && eq(val.getCode());
    }

    @Override
    public String getCode() {
        return this.code;
    }
}
