package com.luohuo.basic.xss.converter;

import cn.hutool.core.util.StrUtil;
import com.luohuo.basic.xss.utils.XssUtils;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;

import java.util.ArrayList;
import java.util.List;

/**
 * 过滤跨站脚本的反序列化工具。
 *
 * @author 乾乾
 */
public class XssStringJsonDeserializer extends ValueDeserializer<String> {
    @Override
    public String deserialize(JsonParser parser, DeserializationContext context) throws JacksonException {
        if (!parser.hasToken(JsonToken.VALUE_STRING)) {
            return null;
        }
        String value = parser.getValueAsString();
        if (StrUtil.isEmpty(value)) {
            return value;
        }

        List<String> list = new ArrayList<>();
        list.add("<script>");
        list.add("</script>");
        list.add("<iframe>");
        list.add("</iframe>");
        list.add("<noscript>");
        list.add("</noscript>");
        list.add("<frameset>");
        list.add("</frameset>");
        list.add("<frame>");
        list.add("</frame>");
        list.add("<noframes>");
        list.add("</noframes>");
        list.add("<embed>");
        list.add("</embed>");
        list.add("<object>");
        list.add("</object>");
        list.add("<meta>");
        list.add("</meta>");
        list.add("<link>");
        list.add("</link>");
        if (list.stream().anyMatch(value::contains)) {
            return XssUtils.xssClean(value, null);
        }
        return value;
    }

    @Override
    public Class<?> handledType() {
        return String.class;
    }
}
