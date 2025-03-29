package com.hula.ai.common.utils;

import com.hula.ai.common.constant.StringPoolConstant;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 错误信息处理类
 *
 * @author: 云裂痕
 * @date: 2023/10/20
 * @version: 1.2.0
 * 得其道
 * 乾乾
 */
public class ExceptionUtil {

    /**
     * 获取exception的详细错误信息。
     */
    public static String getExceptionMessage(Throwable e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw, true));
        String str = sw.toString();
        return str;
    }

    public static String getRootErrorMessage(Exception e) {
        Throwable root = ExceptionUtils.getRootCause(e);
        root = (root == null ? e : root);
        if (root == null) {
            return StringPoolConstant.EMPTY;
        }
        String msg = root.getMessage();
        if (msg == null) {
            return StringPoolConstant.NULL;
        }
        return StringUtils.defaultString(msg);
    }

}
