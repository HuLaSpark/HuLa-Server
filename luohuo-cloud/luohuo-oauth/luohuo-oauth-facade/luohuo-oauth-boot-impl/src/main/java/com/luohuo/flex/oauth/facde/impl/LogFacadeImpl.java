package com.luohuo.flex.oauth.facde.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.luohuo.basic.model.log.OptLogDTO;
import com.luohuo.basic.utils.BeanPlusUtil;
import com.luohuo.flex.base.service.system.BaseOperationLogService;
import com.luohuo.flex.base.vo.save.system.BaseOperationLogSaveVO;
import com.luohuo.flex.oauth.facade.LogFacade;

/**
 * 操作日志保存 API
 *
 * @author 乾乾
 * @date 2019/07/02
 */
@Service
@RequiredArgsConstructor
public class LogFacadeImpl implements LogFacade {
    private final BaseOperationLogService baseOperationLogService;

    /**
     * 保存日志
     *
     * @param data 操作日志
     * @return 操作日志
     */
    public void save(OptLogDTO data) {
        BaseOperationLogSaveVO bean = BeanPlusUtil.toBean(data, BaseOperationLogSaveVO.class);
        baseOperationLogService.save(bean);
    }

}
