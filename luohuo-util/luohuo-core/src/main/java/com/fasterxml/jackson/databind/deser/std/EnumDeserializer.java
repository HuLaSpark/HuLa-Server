package com.fasterxml.jackson.databind.deser.std;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.fasterxml.jackson.databind.cfg.CoercionInputShape;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.type.LogicalType;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.CompactStringObjectMap;
import com.fasterxml.jackson.databind.util.EnumResolver;
import com.luohuo.basic.converter.EnumSerializer;
import com.luohuo.basic.utils.StrPool;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

/**
 * 枚举类反序列化程序类，可以从字符串和整数反序列化指定枚举类的实例。
 * 官方提供的，支持（但不限于）以下接收方式：
 * 1、字符串 如：sex: "M"
 * 2、数字   如：sex: 1    // 数字需要和枚举类的顺序对应，超过会报错。 如Sex 有M、W、N  ，则数字只能是 0、1、2
 * 3、数组   还不清楚怎么使用...
 * <p>
 * <p>
 * 我重写了后，增强了以下功能。
 * 1、传递的枚举类型可以是对象. 如： sex: { "code": "M" }
 * <p>
 * 本类跟jackson-databind包中的EnumDeserializer类同包名，利用类加载机制，会加载此类，不会加载到jackson-databind中的类
 * 参考 BasicDeserializerFactory#1495 行代码
 *
 * @author 乾乾
 * @version 3.2.1
 */
@SuppressWarnings("ALL")
@Deprecated
@JacksonStdImpl // was missing until 2.6
public class EnumDeserializer
        extends StdScalarDeserializer<Object>
        implements ContextualDeserializer {
    private static final long serialVersionUID = 1L;
    protected Object[] _enumsByIndex;
    private final Enum<?> _enumDefaultValue;
    protected final CompactStringObjectMap _lookupByName;
    protected volatile CompactStringObjectMap _lookupByToString;
    protected final Boolean _caseInsensitive;
    private Boolean _useDefaultValueForUnknownEnum;
    private Boolean _useNullForUnknownEnum;
    protected final boolean _isFromIntValue;
    protected final CompactStringObjectMap _lookupByEnumNaming;

    /** @deprecated */
    @Deprecated
    public EnumDeserializer(EnumResolver byNameResolver, Boolean caseInsensitive) {
        this(byNameResolver, caseInsensitive, (EnumResolver) null, (EnumResolver) null);
    }

    /** @deprecated */
    @Deprecated
    public EnumDeserializer(EnumResolver byNameResolver, boolean caseInsensitive, EnumResolver byEnumNamingResolver) {
        super(byNameResolver.getEnumClass());
        this._lookupByName = byNameResolver.constructLookup();
        this._enumsByIndex = byNameResolver.getRawEnums();
        this._enumDefaultValue = byNameResolver.getDefaultValue();
        this._caseInsensitive = caseInsensitive;
        this._isFromIntValue = byNameResolver.isFromIntValue();
        this._lookupByEnumNaming = byEnumNamingResolver == null ? null : byEnumNamingResolver.constructLookup();
        this._lookupByToString = null;
    }

    public EnumDeserializer(EnumResolver byNameResolver, boolean caseInsensitive, EnumResolver byEnumNamingResolver, EnumResolver toStringResolver) {
        super(byNameResolver.getEnumClass());
        this._lookupByName = byNameResolver.constructLookup();
        this._enumsByIndex = byNameResolver.getRawEnums();
        this._enumDefaultValue = byNameResolver.getDefaultValue();
        this._caseInsensitive = caseInsensitive;
        this._isFromIntValue = byNameResolver.isFromIntValue();
        this._lookupByEnumNaming = byEnumNamingResolver == null ? null : byEnumNamingResolver.constructLookup();
        this._lookupByToString = toStringResolver == null ? null : toStringResolver.constructLookup();
    }

    protected EnumDeserializer(EnumDeserializer base, Boolean caseInsensitive, Boolean useDefaultValueForUnknownEnum, Boolean useNullForUnknownEnum) {
        super(base);
        this._lookupByName = base._lookupByName;
        this._enumsByIndex = base._enumsByIndex;
        this._enumDefaultValue = base._enumDefaultValue;
        this._caseInsensitive = caseInsensitive;
        this._isFromIntValue = base._isFromIntValue;
        this._useDefaultValueForUnknownEnum = useDefaultValueForUnknownEnum;
        this._useNullForUnknownEnum = useNullForUnknownEnum;
        this._lookupByEnumNaming = base._lookupByEnumNaming;
        this._lookupByToString = base._lookupByToString;
    }

    /** @deprecated */
    @Deprecated
    protected EnumDeserializer(EnumDeserializer base, Boolean caseInsensitive) {
        this(base, caseInsensitive, (Boolean) null, (Boolean) null);
    }

    /** @deprecated */
    @Deprecated
    public EnumDeserializer(EnumResolver byNameResolver) {
        this((EnumResolver) byNameResolver, (Boolean) null);
    }

    /** @deprecated */
    @Deprecated
    public static JsonDeserializer<?> deserializerForCreator(DeserializationConfig config, Class<?> enumClass, AnnotatedMethod factory) {
        return deserializerForCreator(config, enumClass, factory, (ValueInstantiator) null, (SettableBeanProperty[]) null);
    }

    public static JsonDeserializer<?> deserializerForCreator(DeserializationConfig config, Class<?> enumClass, AnnotatedMethod factory, ValueInstantiator valueInstantiator, SettableBeanProperty[] creatorProps) {
        if (config.canOverrideAccessModifiers()) {
            ClassUtil.checkAndFixAccess(factory.getMember(), config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
        }

        return new FactoryBasedEnumDeserializer(enumClass, factory, factory.getParameterType(0), valueInstantiator, creatorProps);
    }

    public static JsonDeserializer<?> deserializerForNoArgsCreator(DeserializationConfig config, Class<?> enumClass, AnnotatedMethod factory) {
        if (config.canOverrideAccessModifiers()) {
            ClassUtil.checkAndFixAccess(factory.getMember(), config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
        }

        return new FactoryBasedEnumDeserializer(enumClass, factory);
    }

    public EnumDeserializer withResolved(Boolean caseInsensitive, Boolean useDefaultValueForUnknownEnum, Boolean useNullForUnknownEnum) {
        return Objects.equals(this._caseInsensitive, caseInsensitive) && Objects.equals(this._useDefaultValueForUnknownEnum, useDefaultValueForUnknownEnum) && Objects.equals(this._useNullForUnknownEnum, useNullForUnknownEnum) ? this : new EnumDeserializer(this, caseInsensitive, useDefaultValueForUnknownEnum, useNullForUnknownEnum);
    }

    /** @deprecated */
    @Deprecated
    public EnumDeserializer withResolved(Boolean caseInsensitive) {
        return this.withResolved(caseInsensitive, this._useDefaultValueForUnknownEnum, this._useNullForUnknownEnum);
    }

    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
        Boolean caseInsensitive = Optional.ofNullable(findFormatFeature(ctxt, property, handledType(),
                JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)).orElse(_caseInsensitive);
        Boolean useDefaultValueForUnknownEnum = Optional.ofNullable(findFormatFeature(ctxt, property, handledType(),
                JsonFormat.Feature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)).orElse(_useDefaultValueForUnknownEnum);
        Boolean useNullForUnknownEnum = Optional.ofNullable(findFormatFeature(ctxt, property, handledType(),
                JsonFormat.Feature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)).orElse(_useNullForUnknownEnum);
        return this.withResolved(caseInsensitive, useDefaultValueForUnknownEnum, useNullForUnknownEnum);
    }

    public boolean isCachable() {
        return true;
    }

    public LogicalType logicalType() {
        return LogicalType.Enum;
    }

    public Object getEmptyValue(DeserializationContext ctxt) throws JsonMappingException {
        return this._enumDefaultValue;
    }

    public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p.hasToken(JsonToken.VALUE_STRING)) {
            return this._fromString(p, ctxt, p.getText());
        }  else if (p.hasToken(JsonToken.START_OBJECT)) {
            // zuihou 新增的代码！ 支持前端传递对象 {"code": "xx"}
            CompactStringObjectMap lookup = ctxt.isEnabled(DeserializationFeature.READ_ENUMS_USING_TO_STRING)
                    ? _getToStringLookup(ctxt) : _lookupByName;
            JsonNode node = p.getCodec().readTree(p);
            JsonNode code = node.get(EnumSerializer.ALL_ENUM_KEY_FIELD);
            String name = code != null ? code.asText() : node.asText();
            if (StrUtil.isBlank(name) || StrPool.NULL.equals(name)) {
                return null;
            }
            Object result = lookup.find(name);
            if (result == null) {
                return _deserializeAltString(p, ctxt, lookup, name);
            }
            return result;
        } else if (p.hasToken(JsonToken.VALUE_NUMBER_INT)) {
            return this._isFromIntValue ? this._fromString(p, ctxt, p.getText()) : this._fromInteger(p, ctxt, p.getIntValue());
        } else {
            return p.isExpectedStartObjectToken() ? this._fromString(p, ctxt, ctxt.extractScalarFromObject(p, this, this._valueClass)) : this._deserializeOther(p, ctxt);
        }
    }

    protected Object _fromString(JsonParser p, DeserializationContext ctxt, String text) throws IOException {
        CompactStringObjectMap lookup = this._resolveCurrentLookup(ctxt);

        // zuihou 增强
        if (StrUtil.isBlank(text) || StrPool.NULL.equals(text)) {
            return null;
        }

        Object result = lookup.find(text);
        if (result == null) {
            String trimmed = text.trim();
            if (trimmed == text || (result = lookup.find(trimmed)) == null) {
                return this._deserializeAltString(p, ctxt, lookup, trimmed);
            }
        }

        return result;
    }

    private CompactStringObjectMap _resolveCurrentLookup(DeserializationContext ctxt) {
        if (this._lookupByEnumNaming != null) {
            return this._lookupByEnumNaming;
        } else {
            return ctxt.isEnabled(DeserializationFeature.READ_ENUMS_USING_TO_STRING) ? this._getToStringLookup(ctxt) : this._lookupByName;
        }
    }

    protected Object _fromInteger(JsonParser p, DeserializationContext ctxt, int index) throws IOException {
        CoercionAction act = ctxt.findCoercionAction(this.logicalType(), this.handledType(), CoercionInputShape.Integer);
        if (act == CoercionAction.Fail) {
            if (ctxt.isEnabled(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS)) {
                return ctxt.handleWeirdNumberValue(this._enumClass(), index, "not allowed to deserialize Enum value out of number: disable DeserializationConfig.DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS to allow", new Object[0]);
            }

            this._checkCoercionFail(ctxt, act, this.handledType(), index, "Integer value (" + index + ")");
        }

        switch (act) {
            case AsNull:
                return null;
            case AsEmpty:
                return this.getEmptyValue(ctxt);
            case TryConvert:
            default:
                if (index >= 0 && index < this._enumsByIndex.length) {
                    return this._enumsByIndex[index];
                } else if (this.useDefaultValueForUnknownEnum(ctxt)) {
                    return this._enumDefaultValue;
                } else {
                    return !this.useNullForUnknownEnum(ctxt) ? ctxt.handleWeirdNumberValue(this._enumClass(), index, "index value outside legal index range [0..%s]", new Object[]{this._enumsByIndex.length - 1}) : null;
                }
        }
    }

    private final Object _deserializeAltString(JsonParser p, DeserializationContext ctxt, CompactStringObjectMap lookup, String nameOrig) throws IOException {
        String name = nameOrig.trim();
        if (name.isEmpty()) {
            if (this.useDefaultValueForUnknownEnum(ctxt)) {
                return this._enumDefaultValue;
            } else if (this.useNullForUnknownEnum(ctxt)) {
                return null;
            } else {
                CoercionAction act;
                if (nameOrig.isEmpty()) {
                    act = this._findCoercionFromEmptyString(ctxt);
                    act = this._checkCoercionFail(ctxt, act, this.handledType(), nameOrig, "empty String (\"\")");
                } else {
                    act = this._findCoercionFromBlankString(ctxt);
                    act = this._checkCoercionFail(ctxt, act, this.handledType(), nameOrig, "blank String (all whitespace)");
                }

                switch (act) {
                    case AsNull:
                    default:
                        return null;
                    case AsEmpty:
                    case TryConvert:
                        return this.getEmptyValue(ctxt);
                }
            }
        } else {
            if (Boolean.TRUE.equals(this._caseInsensitive)) {
                Object match = lookup.findCaseInsensitive(name);
                if (match != null) {
                    return match;
                }
            }

            if (!ctxt.isEnabled(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS) && !this._isFromIntValue) {
                char c = name.charAt(0);
                if (c >= '0' && c <= '9' && (c != '0' || name.length() <= 1)) {
                    try {
                        int index = Integer.parseInt(name);
                        if (!ctxt.isEnabled(MapperFeature.ALLOW_COERCION_OF_SCALARS)) {
                            return ctxt.handleWeirdStringValue(this._enumClass(), name, "value looks like quoted Enum index, but `MapperFeature.ALLOW_COERCION_OF_SCALARS` prevents use", new Object[0]);
                        }

                        if (index >= 0 && index < this._enumsByIndex.length) {
                            return this._enumsByIndex[index];
                        }
                    } catch (NumberFormatException var8) {
                    }
                }
            }

            if (this.useDefaultValueForUnknownEnum(ctxt)) {
                return this._enumDefaultValue;
            } else {
                return this.useNullForUnknownEnum(ctxt) ? null : ctxt.handleWeirdStringValue(this._enumClass(), name, "not one of the values accepted for Enum class: %s", new Object[]{lookup.keys()});
            }
        }
    }

    protected Object _deserializeOther(JsonParser p, DeserializationContext ctxt) throws IOException {
        return p.hasToken(JsonToken.START_ARRAY) ? this._deserializeFromArray(p, ctxt) : ctxt.handleUnexpectedToken(this._enumClass(), p);
    }

    protected Class<?> _enumClass() {
        return this.handledType();
    }

    /** @deprecated */
    @Deprecated
    protected CompactStringObjectMap _getToStringLookup(DeserializationContext ctxt) {
        CompactStringObjectMap lookup = this._lookupByToString;
        if (lookup == null) {
            synchronized (this) {
                lookup = this._lookupByToString;
                if (lookup == null) {
                    lookup = EnumResolver.constructUsingToString(ctxt.getConfig(), this._enumClass()).constructLookup();
                    this._lookupByToString = lookup;
                }
            }
        }

        return lookup;
    }

    protected boolean useNullForUnknownEnum(DeserializationContext ctxt) {
        return this._useNullForUnknownEnum != null ? this._useNullForUnknownEnum : ctxt.isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL);
    }

    protected boolean useDefaultValueForUnknownEnum(DeserializationContext ctxt) {
        if (this._enumDefaultValue != null) {
            return this._useDefaultValueForUnknownEnum != null ? this._useDefaultValueForUnknownEnum : ctxt.isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE);
        } else {
            return false;
        }
    }
}
