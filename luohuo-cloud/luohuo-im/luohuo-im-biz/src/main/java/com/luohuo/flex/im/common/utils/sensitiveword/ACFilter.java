package com.luohuo.flex.im.common.utils.sensitiveword;

import com.luohuo.flex.im.common.algorithm.ac.ACTrie;
import com.luohuo.flex.im.common.algorithm.ac.MatchResult;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * 基于ac自动机实现的敏感词过滤工具类
 * 为了兼容提供了相同的api接口 {@code hasSensitiveWord}
 * <p>
 *
 * @author berg
 * @date 2023/6/18
 */
public class ACFilter implements SensitiveWordFilter {

    // 替代字符
    private static final char MASK_CHAR = '*';

    private static ACTrie AC_TRIE = null;

    /**
     * 有敏感词
     *
     * @param text 文本
     * @return boolean
     */
    @Override
    public boolean hasSensitiveWord(String text) {
        if (StringUtils.isBlank(text)) { return false; }
        return !Objects.equals(filter(text), text);
    }

    /**
     * 敏感词替换
     *
     * @param text 待替换文本
     * @return 替换后的文本
     */
    @Override
    public String filter(String text) {
        if (StringUtils.isBlank(text)) { return text; }
        List<MatchResult> matchResults = AC_TRIE.matches(text);
        StringBuilder result = new StringBuilder(text);
        // matchResults是按照startIndex排序的，因此可以通过不断更新endIndex最大值的方式算出尚未被替代部分
        int endIndex = 0;
        for (MatchResult matchResult : matchResults) {
            endIndex = Math.max(endIndex, matchResult.getEndIndex());
            replaceBetween(result, matchResult.getStartIndex(), endIndex);
        }
        return result.toString();
    }

    private static void replaceBetween(StringBuilder buffer, int startIndex, int endIndex) {
        for (int i = startIndex; i < endIndex; i++) {
            buffer.setCharAt(i, MASK_CHAR);
        }
    }

    /**
     * 加载敏感词列表
     *
     * @param words 敏感词数组
     */
    @Override
    public void loadWord(List<String> words) {
        if (words == null) { return; }
        AC_TRIE = new ACTrie(words);
    }

}
