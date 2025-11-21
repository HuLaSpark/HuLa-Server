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
 * 本单位及子级
 *
 * @author 乾乾
 * @date 2022/1/9 23:29
 */
@Slf4j
@RequiredArgsConstructor
@Component("DATA_SCOPE_02")
public class CompanyChildrenDataScopeProviderImpl implements DataScopeProvider {
    @Autowired
    private OrgHelperService orgHelperService;

    @Override
    public List<DataFieldProperty> findDataFieldProperty(List<DataFieldProperty> fsp) {
        List<Long> employeeIdList = orgHelperService.findCompanyAndChildrenIdByEmployeeId(ContextUtil.getUid());
        if (CollUtil.isEmpty(employeeIdList)) {
            return Collections.emptyList();
        }
        fsp.forEach(item -> {
            item.setField(SuperEntity.CREATE_ORG_ID_FIELD);
            item.setValues(employeeIdList);
        });
        return fsp;
    }
}
