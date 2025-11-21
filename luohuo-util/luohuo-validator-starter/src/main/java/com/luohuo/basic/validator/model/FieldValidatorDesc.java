package com.luohuo.basic.validator.model;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 字段校验规则信息
 *
 * @author 乾乾
 * @date 2019-07-12 14:28
 */
@Data
@ToString
@Accessors(chain = true)
public class FieldValidatorDesc {
    /**
     * 字段名称
     */
    private String field;
    /**
     * 字段中文
     */
    private String name;
    /**
     * 字段的类型
     */
    private String fieldType;
    /**
     * 约束集合
     */
    private List<ConstraintInfo> constraints;
}
