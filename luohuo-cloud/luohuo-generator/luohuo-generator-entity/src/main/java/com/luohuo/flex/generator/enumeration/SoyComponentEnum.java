package com.luohuo.flex.generator.enumeration;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.luohuo.basic.interfaces.BaseEnum;

import java.util.stream.Stream;

/**
 * soybean 前端组件
 *
 * text | password | textarea | number
 * dict-select| table-select | dict-radio | dict-checkbox | dict-switch
 * dict-cascader
 * datetime | date | time | daterange | datetimerange
 * file-uploader（文件上传） | image-uploader（图片上传） | avatar-uploader（头像上传，单图片） |avatar-cropper (头像裁剪上传)
 * fs-editor-wang | fs-editor-wang5
 * fs-json-editor
 * copyable
 * time-humanize
 *
 * @author 乾乾
 * @date 2022/3/23 20:11
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "生成方式")
public enum SoyComponentEnum implements BaseEnum {
    /**
     * 输入框
     */
    TEXT("text", "输入框"),
    TEXTAREA("textarea", "文本域"),
    PASSWORD("password", "密码框"),
    NUMBER("number", "数字框"),

    DICT_SELECT("dict-select", "字典选择"),
    TABLE_SELECT("table-select", "表格选择"),
    DICT_RADIO("dict-radio", "单选框"),
    DICT_CHECKBOX("dict-checkbox", "复选框"),
    DICT_SWITCH("dict-switch", "开关"),
    DICT_CASCADER("dict-cascader", "级联"),

    DATETIME("datetime", "日期时间"),
    DATE("date", "日期"),
    TIME("time", "时间"),
    DATERANGE("daterange", "日期区间"),
    DATETIMERANGE("datetimerange", "日期时间区间"),

    FILE_UPLOADER("file-uploader", "文件上传"),
    IMAGE_UPLOADER("image-uploader", "图片上传"),
    AVATAR_UPLOADER("avatar-uploader", "头像上传"),
    AVATAR_CROPPER("avatar-cropper", "头像裁剪上传"),

    FS_EDITOR_WANG("fs-editor-wang", "富文本"),
    FS_EDITOR_WANG5("fs-editor-wang5", "富文本"),

    FS_JSON_EDITOR("fs-json-editor", "json"),

    COPYABLE("copyable", "复制"),

    TIME_HUMANIZE("time-humanize", "人性化时间格式"),

    ;

    private String value;
    private String desc;


    /**
     * 根据当前枚举的name匹配
     */
    public static SoyComponentEnum match(String val, SoyComponentEnum def) {
        return Stream.of(values()).parallel().filter(item -> item.name().equalsIgnoreCase(val)).findAny().orElse(def);
    }

    public static SoyComponentEnum get(String val) {
        return match(val, null);
    }

    public boolean eq(SoyComponentEnum val) {
        return val != null && eq(val.name());
    }

    @Override
    @Schema(description = "编码", example = "text")
    public String getCode() {
        return this.value;
    }


}
