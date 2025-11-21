package com.luohuo.flex.datascope.provider;

import cn.hutool.core.collection.CollUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.luohuo.basic.base.entity.SuperEntity;
import com.luohuo.basic.context.ContextUtil;
import com.luohuo.flex.datascope.model.DataFieldProperty;
import com.luohuo.flex.datascope.service.OrgHelperService;

import java.util.Collections;
import java.util.List;

/**
 * 本部门及子级
 *
 * @author 乾乾
 * @date 2022/1/9 23:29
 */
@Slf4j
@RequiredArgsConstructor
@Component("DATA_SCOPE_04")
public class DeptChildrenDataScopeProviderImpl implements DataScopeProvider {
    @Autowired
    private OrgHelperService orgHelperService;

    @Override
    public List<DataFieldProperty> findDataFieldProperty(List<DataFieldProperty> fsp) {
        List<Long> orgIdList = orgHelperService.findDeptAndChildrenIdByEmployeeId(ContextUtil.getUid());
        if (CollUtil.isEmpty(orgIdList)) {
            return Collections.emptyList();
        }
        fsp.forEach(item -> {
            item.setField(SuperEntity.CREATE_ORG_ID_FIELD);
            item.setValues(orgIdList);
        });
        return fsp;
    }
}
