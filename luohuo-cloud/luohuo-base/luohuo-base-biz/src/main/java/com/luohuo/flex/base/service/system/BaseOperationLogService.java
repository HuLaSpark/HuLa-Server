package com.luohuo.flex.base.service.system;

import com.luohuo.basic.base.service.SuperService;
import com.luohuo.flex.base.entity.system.BaseOperationLog;
import com.luohuo.flex.base.vo.result.system.BaseOperationLogResultVO;

import java.time.LocalDateTime;

/**
 * <p>
 * 业务接口
 * 操作日志
 * </p>
 *
 * @author 乾乾
 * @date 2021-11-08
 */
public interface BaseOperationLogService extends SuperService<Long, BaseOperationLog> {
    /**
     * 清理日志
     *
     * @param clearBeforeTime 多久之前的
     * @param clearBeforeNum  多少条
     * @return 是否成功
     */
    boolean clearLog(LocalDateTime clearBeforeTime, Integer clearBeforeNum);


    /**
     * 根据id查询详情
     *
     * @param id id
     * @return com.luohuo.flex.base.vo.result.system.BaseOperationLogResultVO
     * @author tangyh
     * @date 2022/10/13 10:31 AM
     * @create [2022/10/13 10:31 AM ] [tangyh] [初始创建]
     */
    BaseOperationLogResultVO getDetail(Long id);
}
