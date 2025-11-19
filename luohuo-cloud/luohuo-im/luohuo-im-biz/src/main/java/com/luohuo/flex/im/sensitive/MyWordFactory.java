package com.luohuo.flex.im.sensitive;

import com.luohuo.flex.im.common.utils.sensitiveword.IWordFactory;
import com.luohuo.flex.im.sensitive.dao.SensitiveWordDao;
import com.luohuo.flex.im.sensitive.domain.SensitiveWord;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author nyh
 */
@Component
public class MyWordFactory implements IWordFactory {
    @Resource
    private SensitiveWordDao sensitiveWordDao;

    @Override
    public List<String> getWordList() {
        return sensitiveWordDao.list()
                .stream()
                .map(SensitiveWord::getWord)
                .collect(Collectors.toList());
    }
}
