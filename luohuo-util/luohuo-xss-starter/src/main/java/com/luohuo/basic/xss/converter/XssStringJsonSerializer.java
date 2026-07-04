package com.luohuo.basic.xss.converter;

import cn.hutool.core.util.StrUtil;
import com.luohuo.basic.xss.utils.XssUtils;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ser.std.StdSerializer;

/**
 * 基于 XSS 的 JSON 序列化器。
 *
 * @author 乾乾
 * @date 2019/06/28
 */
@Slf4j
public class XssStringJsonSerializer extends StdSerializer<String> {
    public XssStringJsonSerializer() {
        super(String.class);
    }

    @Override
    public void serialize(String value, JsonGenerator jsonGenerator,
                          SerializationContext context) throws JacksonException {
        if (StrUtil.isEmpty(value)) {
            return;
        }
        try {
            String encodedValue = XssUtils.xssClean(value, null);
            jsonGenerator.writeString(encodedValue);
        } catch (Exception e) {
            log.error("序列化失败:[{}]", value, e);
        }
    }
}
