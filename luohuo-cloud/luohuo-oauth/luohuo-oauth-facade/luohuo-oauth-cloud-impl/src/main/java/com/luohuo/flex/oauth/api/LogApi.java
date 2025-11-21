package com.luohuo.flex.oauth.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.luohuo.basic.constant.Constants;
import com.luohuo.basic.model.log.OptLogDTO;

/**
 * 操作日志保存 API
 *
 * @author 乾乾
 * @date 2019/07/02
 */
@FeignClient(name = "${" + Constants.PROJECT_PREFIX + ".feign.oauth-server:luohuo-oauth-server}")
public interface LogApi {

    /**
     * 保存日志
     *
     * @param log 操作日志
     */
    @RequestMapping(value = "/optLog", method = RequestMethod.POST)
    void save(@RequestBody OptLogDTO log);

}
