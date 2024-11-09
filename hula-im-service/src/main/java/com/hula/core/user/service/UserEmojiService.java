package com.hula.core.user.service;


import com.hula.common.domain.vo.res.IdRespVO;
import com.hula.core.user.domain.vo.req.user.UserEmojiReq;
import com.hula.core.user.domain.vo.resp.user.UserEmojiResp;
import com.hula.domain.vo.res.ApiResult;

import java.util.List;

/**
 * 用户表情包 Service
 *
 * @author: nyh
 */
public interface UserEmojiService {

    /**
     * 表情包列表
     *
     * @return 表情包列表
     * @author WuShiJie
     * @createTime 2023/7/3 14:46
     **/
    List<UserEmojiResp> list(Long uid);

    /**
     * 新增表情包
     *
     * @param emojis 用户表情包
     * @param uid    用户ID
     * @return 表情包
     * @author WuShiJie
     * @createTime 2023/7/3 14:46
     **/
    ApiResult<IdRespVO> insert(UserEmojiReq emojis, Long uid);

    /**
     * 删除表情包
     *
     * @param id
     * @param uid
     */
    void remove(Long id, Long uid);
}
