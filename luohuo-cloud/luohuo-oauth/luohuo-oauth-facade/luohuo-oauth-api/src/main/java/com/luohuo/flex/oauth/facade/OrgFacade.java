package com.luohuo.flex.oauth.facade;

import com.luohuo.basic.interfaces.echo.LoadService;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * 部门回显
 *
 * @author 乾乾
 * @date 2019/07/26
 */
public interface OrgFacade extends LoadService {

    /**
     * 根据id查询实体
     *
     * @param ids 唯一键（可能不是主键ID)
     * @return
     */
    @Override
    Map<Serializable, Object> findByIds(Set<Serializable> ids);

}
