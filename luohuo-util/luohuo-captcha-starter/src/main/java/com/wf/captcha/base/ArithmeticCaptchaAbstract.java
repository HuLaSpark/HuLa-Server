package com.wf.captcha.base;

import com.googlecode.aviator.AviatorEvaluator;

/**
 * 算术验证码抽象类
 *
 * @author Created by 王帆 on 2019-08-23 上午 10:08.
 */
public abstract class ArithmeticCaptchaAbstract extends Captcha {
    private String arithmeticString;  // 计算公式

    public ArithmeticCaptchaAbstract() {
        setLen(2);
    }

    /**
     * 生成随机验证码
     *
     * @return 验证码字符数组
     */
    @Override
    protected char[] alphas() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            sb.append(num(10));
            if (i < len - 1) {
                int type = num(1, 4);
                if (type == 1) {
                    sb.append("+");
                } else if (type == 2) {
                    sb.append("-");
                } else if (type == 3) {
                    sb.append("x");
                }
            }
        }
        chars = String.valueOf(AviatorEvaluator.execute(sb.toString().replace("x", "*")));
        sb.append("=?");
        arithmeticString = sb.toString();
        return chars.toCharArray();
    }

    public String getArithmeticString() {
        checkAlpha();
        return arithmeticString;
    }

    public void setArithmeticString(String arithmeticString) {
        this.arithmeticString = arithmeticString;
    }
}
