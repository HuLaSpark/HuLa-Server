package com.luohuo.flex.job.facade;

import com.luohuo.basic.base.R;
import com.luohuo.flex.job.dto.XxlJobInfoVO;

/**
 * @author 乾乾
 * @since 2024年09月21日00:15:26
 */
public interface JobFacade {
    /**
     * 定时发送接口
     *
     * @param xxlJobInfo 任务
     * @return 任务id
     */
    R<String> addTimingTask(XxlJobInfoVO xxlJobInfo);

}
