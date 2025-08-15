package com.luohuo.flex.im.core.user.service;

import com.luohuo.basic.base.R;
import com.luohuo.flex.im.domain.vo.req.user.UserEmojiReq;
import com.luohuo.flex.im.domain.vo.res.IdRespVO;
import com.luohuo.flex.im.domain.vo.resp.user.UserEmojiResp;

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
     * @date 2023/7/3 14:46
     **/
    List<UserEmojiResp> list(Long uid);

    /**
     * 新增表情包
     *
     * @param emojis 用户表情包
     * @param uid    用户ID
     * @return 表情包
     * @author WuShiJie
     * @date 2023/7/3 14:46
     **/
	R<IdRespVO> insert(UserEmojiReq emojis, Long uid);

    /**
     * 删除表情包
     *
     * @param id
     * @param uid
     */
    void remove(Long id, Long uid);
}
