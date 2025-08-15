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
 * 附件
 * </p>
 *
 * @author zuihou
 * @date 2020-11-20
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "文件类型-枚举")
public enum FileType implements BaseEnum {

    /**
     * IMAGE="图片"
     */
    IMAGE("图片"),
    /**
     * VIDEO="视频"
     */
    VIDEO("视频"),
    /**
     * AUDIO="音频"
     */
    AUDIO("音频"),
    /**
     * DOC="文档"
     */
    DOC("文档"),
    /**
     * OTHER="其他"
     */
    OTHER("其他"),
    ;

    @Schema(description = "描述")
    private String desc;


    /**
     * 根据当前枚举的name匹配
     */
    public static FileType match(String val, FileType def) {
        return Stream.of(values()).parallel().filter(item -> item.name().equalsIgnoreCase(val)).findAny().orElse(def);
    }

    public static FileType get(String val) {
        return match(val, null);
    }

    public boolean eq(FileType val) {
        return val != null && eq(val.name());
    }

    @Override
    @Schema(description = "编码", allowableValues = "IMAGE,VIDEO,AUDIO,DOC,OTHER", example = "IMAGE")
    public String getCode() {
        return this.name();
    }

}
