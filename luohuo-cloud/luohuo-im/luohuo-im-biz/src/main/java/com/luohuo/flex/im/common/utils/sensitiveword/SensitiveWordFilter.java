package com.luohuo.flex.im.common.utils.sensitiveword;


import java.util.List;

/**
 * 敏感词过滤
 *
 * @author nyh
 */
public interface SensitiveWordFilter {
    /**
     * 有敏感词
     *
     * @param text 文本
     * @return boolean
     */
    boolean hasSensitiveWord(String text);

    /**
     * 过滤
     *
     * @param text 文本
     * @return {@link String}
     */
    String filter(String text);

    /**
     * 加载敏感词列表
     *
     * @param words 敏感词数组
     */
    void loadWord(List<String> words);


}
