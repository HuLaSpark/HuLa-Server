package com.luohuo.basic.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Component;
import com.luohuo.basic.domain.entity.SecureInvokeRecord;
import com.luohuo.basic.mapper.SecureInvokeRecordMapper;
import com.luohuo.basic.service.SecureInvokeService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Description:
 * Date: 2023-08-06
 */
@Component
public class SecureInvokeRecordDao extends ServiceImpl<SecureInvokeRecordMapper, SecureInvokeRecord> {

    public List<SecureInvokeRecord> getWaitRetryRecords() {
        LocalDateTime now = LocalDateTime.now();
        return lambdaQuery()
                .eq(SecureInvokeRecord::getState, SecureInvokeRecord.STATUS_WAIT)
                .lt(SecureInvokeRecord::getNextRetryTime, now)
				//查2分钟前的失败数据。避免刚入库的数据被查出来
                .lt(SecureInvokeRecord::getCreateTime, now.plusMinutes(- (long) SecureInvokeService.RETRY_INTERVAL_MINUTES))
                .list();
    }
}
