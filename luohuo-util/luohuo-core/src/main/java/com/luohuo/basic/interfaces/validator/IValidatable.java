package com.luohuo.basic.interfaces.validator;

/**
 * 实现了此接口，表示此类将会支持验证框架。
 *
 * @author 乾乾
 * @date 2020年02月02日20:42:34
 */
public interface IValidatable {

    /**
     * 此类需要检验什么值
     * 支持length长度检验。也可以看情况实现支持类似于email，正则等等校验
     *
     * @return 需要验证的值
     */
    Object value();
}
