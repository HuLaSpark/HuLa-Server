package com.luohuo.basic.converter;

import cn.hutool.core.util.StrUtil;
import com.luohuo.basic.interfaces.BaseEnum;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ser.std.StdSerializer;

/**
 * 继承了BaseEnum接口的枚举值，将会统一按照以下格式序列化。
 *
 * @author 乾乾
 * @date 2020/5/4 下午6:45
 * @see 4.6.0
 */
@Deprecated
public class EnumSerializer extends StdSerializer<BaseEnum> {
    public static final EnumSerializer INSTANCE = new EnumSerializer();
    public static final String ALL_ENUM_KEY_FIELD = "code";
    public static final String ALL_ENUM_EXTRA_FIELD = "extra";
    public static final String ALL_ENUM_DESC_FIELD = "desc";

    public EnumSerializer() {
        super(BaseEnum.class);
    }

    @Override
    public void serialize(BaseEnum distance, JsonGenerator generator, SerializationContext context)
            throws JacksonException {
        generator.writeStartObject();
        generator.writeName(ALL_ENUM_KEY_FIELD);
        generator.writeString(distance.getCode());
        generator.writeName(ALL_ENUM_DESC_FIELD);
        generator.writeString(distance.getDesc());
        if (StrUtil.isNotEmpty(distance.getExtra())) {
            generator.writeName(ALL_ENUM_EXTRA_FIELD);
            generator.writeString(distance.getExtra());
        }
        generator.writeEndObject();
    }
}
