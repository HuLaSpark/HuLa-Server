package com.luohuo.flex.job.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.luohuo.basic.base.R;
import com.luohuo.basic.constant.Constants;
import com.luohuo.flex.job.dto.XxlJobInfoVO;

/**
 * @author 乾乾
 * @date 2021/1/4 11:52 下午
 */
@FeignClient(name = "JobApi", url = "${" + Constants.PROJECT_PREFIX + ".feign.job-server:http://127.0.0.1:8767}", path = "/xxl-job-admin")
public interface JobApi {
    /**
     * 定时发送接口
     *
     * @param xxlJobInfo
     * @return
     */
    @RequestMapping(value = "/jobinfo/save", method = RequestMethod.POST)
    R<String> addTimingTask(@RequestBody XxlJobInfoVO xxlJobInfo);

}
