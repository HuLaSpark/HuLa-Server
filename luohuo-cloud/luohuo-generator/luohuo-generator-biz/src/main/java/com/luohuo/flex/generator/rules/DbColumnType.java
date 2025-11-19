package com.luohuo.flex.generator.rules;

import com.luohuo.flex.generator.enumeration.ComponentEnum;
import com.luohuo.flex.generator.enumeration.SoyComponentEnum;
import com.luohuo.flex.generator.enumeration.VxeComponentEnum;

/**
 * @author 乾乾
 * @date 2022/3/13 23:02
 */
public enum DbColumnType implements ColumnType {
    // 基本类型
    BASE_BYTE("byte", null, "string", ComponentEnum.PLUS_INPUT.getValue(), VxeComponentEnum.$INPUT.getValue(), SoyComponentEnum.TEXT.getValue()),
    BASE_SHORT("short", null, "number", ComponentEnum.PLUS_INPUT.getValue(), VxeComponentEnum.$INPUT.getValue(), SoyComponentEnum.TEXT.getValue()),
    BASE_CHAR("char", null, "string", ComponentEnum.PLUS_INPUT.getValue(), VxeComponentEnum.$INPUT.getValue(), SoyComponentEnum.TEXT.getValue()),
    BASE_INT("int", null, "number", ComponentEnum.PLUS_INPUT.getValue(), VxeComponentEnum.$INPUT.getValue(), SoyComponentEnum.TEXT.getValue()),
    BASE_LONG("long", null, "string", ComponentEnum.PLUS_INPUT.getValue(), VxeComponentEnum.$INPUT.getValue(), SoyComponentEnum.TEXT.getValue()),
    BASE_FLOAT("float", null, "number", ComponentEnum.PLUS_INPUT.getValue(), VxeComponentEnum.$INPUT.getValue(), SoyComponentEnum.TEXT.getValue()),
    BASE_DOUBLE("double", null, "number", ComponentEnum.PLUS_INPUT.getValue(), VxeComponentEnum.$INPUT.getValue(), SoyComponentEnum.TEXT.getValue()),
    BASE_BOOLEAN("boolean", null, "boolean", ComponentEnum.PLUS_RADIO_GROUP.getValue(), VxeComponentEnum.$RADIO.getValue(), SoyComponentEnum.DICT_RADIO.getValue()),

    // 包装类型
    BYTE("Byte", null, "string", ComponentEnum.PLUS_INPUT.getValue(), VxeComponentEnum.$INPUT.getValue(), SoyComponentEnum.TEXT.getValue()),
    SHORT("Short", null, "number", ComponentEnum.PLUS_INPUT.getValue(), VxeComponentEnum.$INPUT.getValue(), SoyComponentEnum.TEXT.getValue()),
    CHARACTER("Character", null, "string", ComponentEnum.PLUS_INPUT.getValue(), VxeComponentEnum.$INPUT.getValue(), SoyComponentEnum.TEXT.getValue()),
    INTEGER("Integer", null, "number", ComponentEnum.PLUS_INPUT.getValue(), VxeComponentEnum.$INPUT.getValue(), SoyComponentEnum.TEXT.getValue()),
    LONG("Long", null, "string", ComponentEnum.PLUS_INPUT.getValue(), VxeComponentEnum.$INPUT.getValue(), SoyComponentEnum.TEXT.getValue()),
    FLOAT("Float", null, "number", ComponentEnum.PLUS_INPUT.getValue(), VxeComponentEnum.$INPUT.getValue(), SoyComponentEnum.TEXT.getValue()),
    DOUBLE("Double", null, "number", ComponentEnum.PLUS_INPUT.getValue(), VxeComponentEnum.$INPUT.getValue(), SoyComponentEnum.TEXT.getValue()),
    BOOLEAN("Boolean", null, "boolean", ComponentEnum.PLUS_RADIO_GROUP.getValue(), VxeComponentEnum.$RADIO.getValue(), SoyComponentEnum.DICT_RADIO.getValue()),
    STRING("String", null, "string", ComponentEnum.PLUS_INPUT.getValue(), VxeComponentEnum.$INPUT.getValue(), SoyComponentEnum.TEXT.getValue()),

    // sql 包下数据类型
    DATE_SQL("Date", "java.sql.Date", "string", ComponentEnum.PLUS_DATE_PICKER.getValue(), VxeComponentEnum.$INPUT.getValue(), SoyComponentEnum.DATETIME.getValue()),
    TIME("Time", "java.sql.Time", "string", ComponentEnum.PLUS_DATE_PICKER.getValue(), VxeComponentEnum.$INPUT.getValue(), SoyComponentEnum.TIME.getValue()),
    TIMESTAMP("Timestamp", "java.sql.Timestamp", "string", ComponentEnum.PLUS_DATE_PICKER.getValue(), VxeComponentEnum.$INPUT.getValue(), SoyComponentEnum.DATE.getValue()),
    BLOB("Blob", "java.sql.Blob", "string", ComponentEnum.PLUS_INPUT.getValue(), VxeComponentEnum.$INPUT.getValue(), SoyComponentEnum.TEXTAREA.getValue()),
    CLOB("Clob", "java.sql.Clob", "string", ComponentEnum.PLUS_INPUT.getValue(), VxeComponentEnum.$INPUT.getValue(), SoyComponentEnum.TEXTAREA.getValue()),

    // java8 新时间类型
    LOCAL_DATE("LocalDate", "java.time.LocalDate", "string", ComponentEnum.PLUS_DATE_PICKER.getValue(), VxeComponentEnum.$INPUT.getValue(), SoyComponentEnum.DATE.getValue()),
    LOCAL_TIME("LocalTime", "java.time.LocalTime", "string", ComponentEnum.PLUS_TIME_PICKER.getValue(), VxeComponentEnum.$INPUT.getValue(), SoyComponentEnum.TIME.getValue()),
    YEAR("Year", "java.time.Year", "string", ComponentEnum.PLUS_DATE_PICKER.getValue(), VxeComponentEnum.$INPUT.getValue(), SoyComponentEnum.DATE.getValue()),
    YEAR_MONTH("YearMonth", "java.time.YearMonth", "string", ComponentEnum.PLUS_MONTH_PICKER.getValue(), VxeComponentEnum.$INPUT.getValue(), SoyComponentEnum.DATE.getValue()),
    LOCAL_DATE_TIME("LocalDateTime", "java.time.LocalDateTime", "string", ComponentEnum.PLUS_DATE_PICKER.getValue(), VxeComponentEnum.$INPUT.getValue(), SoyComponentEnum.DATETIME.getValue()),
    INSTANT("Instant", "java.time.Instant", "string", ComponentEnum.PLUS_INPUT.getValue(), VxeComponentEnum.$INPUT.getValue(), SoyComponentEnum.TEXT.getValue()),

    // 其他杂类
    MAP("Map", "java.util.Map", "any", ComponentEnum.PLUS_INPUT.getValue(), VxeComponentEnum.$INPUT.getValue(), SoyComponentEnum.TEXT.getValue()),
    BYTE_ARRAY("byte[]", null, "string", ComponentEnum.PLUS_INPUT.getValue(), VxeComponentEnum.$INPUT.getValue(), SoyComponentEnum.TEXT.getValue()),
    OBJECT("Object", null, "any", ComponentEnum.PLUS_INPUT.getValue(), VxeComponentEnum.$INPUT.getValue(), SoyComponentEnum.TEXT.getValue()),
    DATE("Date", "java.util.Date", "string", ComponentEnum.PLUS_DATE_PICKER.getValue(), VxeComponentEnum.$INPUT.getValue(), SoyComponentEnum.DATETIME.getValue()),
    BIG_INTEGER("BigInteger", "java.math.BigInteger", "string", ComponentEnum.PLUS_INPUT.getValue(), VxeComponentEnum.$INPUT.getValue(), SoyComponentEnum.NUMBER.getValue()),
    BIG_DECIMAL("BigDecimal", "java.math.BigDecimal", "string", ComponentEnum.PLUS_INPUT.getValue(), VxeComponentEnum.$INPUT.getValue(), SoyComponentEnum.NUMBER.getValue());

    /**
     * 类型
     */
    private final String type;

    /**
     * 包路径
     */
    private final String pkg;
    private final String tsType;
    private final String component;
    private final String vxeComponent;
    private final String soyComponent;

    DbColumnType(final String type, final String pkg, final String tsType, final String component, String vxeComponent, String soyComponent) {
        this.type = type;
        this.pkg = pkg;
        this.tsType = tsType;
        this.component = component;
        this.vxeComponent = vxeComponent;
        this.soyComponent = soyComponent;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getPkg() {
        return pkg;
    }

    @Override
    public String getTsType() {
        return this.tsType;
    }

    @Override
    public String getComponent() {
        return component;
    }

    @Override
    public String getVxeComponent() {
        return vxeComponent;
    }

    @Override
    public String getSoyComponent() {
        return soyComponent;
    }


}
