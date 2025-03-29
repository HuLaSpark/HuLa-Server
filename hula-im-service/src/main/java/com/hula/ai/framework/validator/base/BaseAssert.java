package com.hula.ai.framework.validator.base;

import cn.hutool.core.util.ObjectUtil;
import com.hula.ai.common.constant.StringPoolConstant;
import com.hula.ai.common.enums.ResponseEnum;
import com.hula.ai.common.exception.ValidateException;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 数据校验返回错误提示
 *
 * @author: 云裂痕
 * @date: 2019/8/16
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
public abstract class BaseAssert {

    /**
     * 判断参数是否存在
     *
     * @param object  对象
     * @param message 返回提示
     */
    public static void isBlankOrNull(Object object, String... message) {
        if (ObjectUtil.isNull(object)) {
            throw new ValidateException(ObjectUtil.isNotNull(message) ? message[0] : ResponseEnum.BAD_REQUEST.getMsg());
        }
    }

    /**
     * 获取请求体中指定参数
     *
     * @param maps    map结构对象
     * @param param   参数
     * @param message 为null时返回提示
     * @return String
     */
    public static String getParam(Map<String, Object> maps, String param, String... message) {
        isBlankOrNull(maps.get(param), ObjectUtil.isNotNull(message) ? message[0] : ResponseEnum.BAD_REQUEST.getMsg());
        return maps.get(param).toString();
    }

    /**
     * 获取请求体中指定参数
     *
     * @param maps    map结构对象
     * @param param   参数
     * @param message 为null时返回提示
     * @return Integer
     */
    public static Integer getParamInt(Map<String, Object> maps, String param, String... message) {
        return Integer.valueOf(getParam(maps, param, message));
    }

    /**
     * 获取请求体中指定参数
     *
     * @param maps    map结构对象
     * @param param   参数
     * @param message 为null时返回提示
     * @return Long
     */
    public static Long getParamLong(Map<String, Object> maps, String param, String... message) {
        return Long.valueOf(getParam(maps, param, message));
    }


    /**
     * 获取请求体中指定参数
     *
     * @param maps    map结构对象
     * @param param   参数
     * @param message 为null时返回提示
     * @return Double
     */
    public static Double getParamDouble(Map<String, Object> maps, String param, String... message) {
        return Double.valueOf(getParam(maps, param, message));
    }

    /**
     * 获取请求体中指定参数
     *
     * @param maps    map结构对象
     * @param param   参数
     * @param message 为null时返回提示
     * @return Boolean
     */
    public static Boolean getParamBoolean(Map<String, Object> maps, String param, String... message) {
        return Boolean.valueOf(getParam(maps, param, message));
    }

    /**
     * 获取请求体中指定参数
     *
     * @param maps    map结构对象
     * @param param   参数
     * @param message 为null时返回提示
     * @return BigDecimal
     */
    public static BigDecimal getParamBigDecimal(Map<String, Object> maps, String param, String... message) {
        return new BigDecimal(getParam(maps, param, message));
    }

    /**
     * 获取请求体中指定参数
     *
     * @param maps    map结构对象
     * @param param   参数
     * @param message 为null时返回提示
     * @return Boolean
     */
    public static String getParamString(Map<String, String> maps, String param, String... message) {
        isBlankOrNull(maps.get(param), ObjectUtil.isNotNull(message) ? message[0] : ResponseEnum.BAD_REQUEST.getMsg());
        return maps.get(param);
    }

    /**
     * 判断参数是否存在否则返回默认空字符串
     *
     * @param maps  map结构对象
     * @param param 参数
     */
    public static String getParamOrElse(Map<String, Object> maps, String param) {
        if (ObjectUtil.isNull(maps.get(param))) {
            return StringPoolConstant.EMPTY;
        }
        return maps.get(param).toString();
    }

    /**
     * 判断参数是否存在否则返回指定字符串
     *
     * @param maps  map结构对象
     * @param param 参数
     * @param other 返回参数
     */
    public static String getParamOrElse(Map<String, Object> maps, String param, String other) {
        if (ObjectUtil.isNull(maps.get(param))) {
            return other;
        }
        return maps.get(param).toString();
    }

}
