package com.luohuo.flex.im.domain.dto.captcha;

import java.security.SecureRandom;

/**
 * 随机数工具类
 * Created by 乾乾 on 2024-07-27 上午 10:08.
 */
public class Randoms {
    /**
     * 定义验证码字符.去除了0、O、I、L等容易混淆的字母
     */
    public static final char[] ALPHA = {'2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'm', 'n', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
    protected static final SecureRandom RANDOM = new SecureRandom();
    /**
     * 数字的最大索引，不包括最大值
     */
    protected static final int numMaxIndex = 8;
    /**
     * 字符的最小索引，包括最小值
     */
    protected static final int charMinIndex = numMaxIndex;
    /**
     * 大写字符最小索引
     */
    protected static final int upperMinIndex = charMinIndex;
    /**
     * 大写字符最大索引
     */
    protected static final int upperMaxIndex = upperMinIndex + 23;
    /**
     * 小写字母最小索引
     */
    protected static final int lowerMinIndex = upperMaxIndex;
    /**
     * 字符的最大索引，不包括最大值
     */
    protected static final int charMaxIndex = ALPHA.length;
    /**
     * 小写字母最大索引
     */
    protected static final int lowerMaxIndex = charMaxIndex;

    /**
     * 产生两个数之间的随机数
     *
     * @param min 最小值
     * @param max 最大值
     * @return 随机数
     */
    public static int num(int min, int max) {
        return min + RANDOM.nextInt(max - min);
    }

    /**
     * 产生0-num的随机数,不包括num
     *
     * @param num 最大值
     * @return 随机数
     */
    public static int num(int num) {
        return RANDOM.nextInt(num);
    }

    /**
     * 返回ALPHA中的随机字符
     *
     * @return 随机字符
     */
    public static char alpha() {
        return ALPHA[num(ALPHA.length)];
    }

    /**
     * 返回ALPHA中第0位到第num位的随机字符
     *
     * @param num 到第几位结束
     * @return 随机字符
     */
    public static char alpha(int num) {
        return ALPHA[num(num)];
    }

    /**
     * 返回ALPHA中第min位到第max位的随机字符
     *
     * @param min 从第几位开始
     * @param max 到第几位结束
     * @return 随机字符
     */
    public static char alpha(int min, int max) {
        return ALPHA[num(min, max)];
    }
}
