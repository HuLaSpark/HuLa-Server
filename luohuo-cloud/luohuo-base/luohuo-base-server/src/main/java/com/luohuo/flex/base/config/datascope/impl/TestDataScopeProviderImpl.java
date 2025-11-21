package com.luohuo.flex.base.config.datascope.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import com.luohuo.flex.datascope.model.DataFieldProperty;
import com.luohuo.flex.datascope.provider.DataScopeProvider;

import java.util.Arrays;
import java.util.List;

/**
 * 自定义实现类，可以自己实现逻辑
 *
 * @author 乾乾
 * @date 2022/1/11 22:42
 */
@Slf4j
@RequiredArgsConstructor
@Component("DATA_SCOPE_TEST")
public class TestDataScopeProviderImpl implements DataScopeProvider {
    @Override
    public List<DataFieldProperty> findDataFieldProperty(List<DataFieldProperty> fsp) {
        List<Long> orgIdList = Arrays.asList(1L, 2L);
        // and {别名1}.biz_id in (1, 2) [and {别名N}.biz_id in (1, 2)...]
        fsp.forEach(item -> {
            item.setField("biz_id");
            item.setValues(orgIdList);
        });
        return fsp;
    }
}
