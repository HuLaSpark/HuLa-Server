package com.luohuo.flex.oauth.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.luohuo.basic.constant.Constants;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * 岗位API
 *
 * @author zuihou
 * @date 2019/08/02
 */
@FeignClient(name = "${" + Constants.PROJECT_PREFIX + ".feign.oauth-server:luohuo-oauth-server}")
public interface PositionApi {

    /**
     * 根据id查询实体
     *
     * @param ids 唯一键（可能不是主键ID)
     * @return
     */
    @PostMapping("/echo/position/findByIds")
    Map<Serializable, Object> findByIds(@RequestParam(value = "ids") Set<Serializable> ids);
}
