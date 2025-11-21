package com.luohuo.flex.im.facade;

import com.luohuo.basic.interfaces.echo.LoadService;
import com.luohuo.flex.im.domain.vo.resp.user.UserInfoResp;

/**
 * 用户
 *
 * @author 乾乾
 * @date 2019/07/02
 */
public interface ImUserFacade extends LoadService {

    /**
     * 根据id查询实体
     *
     * @param id 唯一键
     * @return
     */
	UserInfoResp getUserInfo(Long id);
}
