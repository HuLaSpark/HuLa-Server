package com.luohuo.flex.im.core.user.service.impl;

import com.luohuo.flex.im.domain.vo.res.IdRespVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.luohuo.basic.base.R;
import com.luohuo.basic.validator.utils.AssertUtil;
import com.luohuo.flex.model.redis.annotation.RedissonLock;
import com.luohuo.flex.im.core.user.dao.UserEmojiDao;
import com.luohuo.flex.im.domain.entity.UserEmoji;
import com.luohuo.flex.im.domain.vo.req.user.UserEmojiReq;
import com.luohuo.flex.im.domain.vo.resp.user.UserEmojiResp;
import com.luohuo.flex.im.core.user.service.UserEmojiService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户表情包 ServiceImpl
 * @author nyh
 */
@Service
@Slf4j
public class UserEmojiServiceImpl implements UserEmojiService {

    @Resource
    private UserEmojiDao userEmojiDao;

    @Override
    public List<UserEmojiResp> list(Long uid) {
        return userEmojiDao.listByUid(uid).
                stream()
                .map(a -> UserEmojiResp.builder()
                        .id(a.getId())
                        .expressionUrl(a.getExpressionUrl())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 新增表情包
     *
     * @param uid 用户ID
     * @return 表情包
     * @author WuShiJie
     * @createTime 2023/7/3 14:46
     **/
    @Override
    @RedissonLock(key = "#uid")
    public R<IdRespVO> insert(UserEmojiReq req, Long uid) {
        //校验表情数量是否超过30
//        int count = userEmojiDao.countByUid(uid);
//        AssertUtil.isFalse(count > 30, "最多只能添加30个表情哦~~");
        //校验表情是否存在
        int existsCount = Math.toIntExact(userEmojiDao.lambdaQuery()
                .eq(UserEmoji::getExpressionUrl, req.getExpressionUrl())
                .eq(UserEmoji::getUid, uid)
                .count());
        AssertUtil.isFalse(existsCount > 0, "当前表情已存在哦~~");
        UserEmoji insert = UserEmoji.builder().uid(uid).expressionUrl(req.getExpressionUrl()).build();
        userEmojiDao.save(insert);
        return R.success(IdRespVO.builder().id(insert.getId()).build());
    }

    @Override
    public void remove(Long id, Long uid) {
        UserEmoji userEmoji = userEmojiDao.getById(id);
        AssertUtil.isNotEmpty(userEmoji, "表情不能为空");
        AssertUtil.equal(userEmoji.getUid(), uid, "小黑子，别人表情不是你能删的");
        userEmojiDao.removeById(id);
    }
}
