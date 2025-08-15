package com.luohuo.flex.base.manager.tenant.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.luohuo.flex.common.utils.ToolsUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.luohuo.basic.base.manager.impl.SuperCacheManagerImpl;
import com.luohuo.basic.cache.redis2.CacheResult;
import com.luohuo.basic.database.mybatis.conditions.Wraps;
import com.luohuo.basic.model.cache.CacheKey;
import com.luohuo.basic.model.cache.CacheKeyBuilder;
import com.luohuo.basic.utils.ArgumentAssert;
import com.luohuo.basic.utils.CollHelper;
import com.luohuo.flex.base.entity.tenant.DefUser;
import com.luohuo.flex.base.manager.tenant.DefUserManager;
import com.luohuo.flex.base.mapper.tenant.DefUserMapper;
import com.luohuo.flex.base.vo.query.tenant.DefUserPageQuery;
import com.luohuo.flex.base.vo.query.tenant.DefUserResultVO;
import com.luohuo.flex.common.cache.tenant.base.DefUserCacheKeyBuilder;
import com.luohuo.flex.common.cache.tenant.base.DefUserEmailCacheKeyBuilder;
import com.luohuo.flex.common.cache.tenant.base.DefUserIdCardCacheKeyBuilder;
import com.luohuo.flex.common.cache.tenant.base.DefUserMobileCacheKeyBuilder;
import com.luohuo.flex.common.cache.tenant.base.DefUserUserNameCacheKeyBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 应用管理
 *
 * @author tangyh
 * @version v1.0
 * @date 2021/9/29 1:26 下午
 * @create [2021/9/29 1:26 下午 ] [tangyh] [初始创建]
 */
@RequiredArgsConstructor
@Service
public class DefUserManagerImpl extends SuperCacheManagerImpl<DefUserMapper, DefUser> implements DefUserManager {
    @Override
    protected CacheKeyBuilder cacheKeyBuilder() {
        return new DefUserCacheKeyBuilder();
    }

    @Transactional(readOnly = true)
    @Override
    
    public Map<Serializable, Object> findByIds(Set<Serializable> ids) {
        List<DefUser> list = findByIds(ids, null).stream().filter(Objects::nonNull).toList();
        return CollHelper.uniqueIndex(list, DefUser::getId, DefUser::getNickName);
    }


    @Override
    public IPage<DefUserResultVO> pageUser(DefUserPageQuery pageQuery, IPage<DefUser> page) {
        return baseMapper.pageUser(pageQuery, page);
    }

    @Override
    public int resetPassErrorNum(Long id) {
        return baseMapper.resetPassErrorNum(id, LocalDateTime.now());
    }

    @Override
    public void incrPasswordErrorNumById(Long id) {
        baseMapper.incrPasswordErrorNumById(id, LocalDateTime.now());
    }


    @Override
    public boolean checkUsername(String value, Long id) {
        return count(Wraps.<DefUser>lbQ().eq(DefUser::getUsername, value).ne(DefUser::getId, id)) > 0;
    }

    @Override
    public boolean checkEmail(String value, Long id) {
        return count(Wraps.<DefUser>lbQ().eq(DefUser::getEmail, value).ne(DefUser::getId, id)) > 0;
    }

    @Override
    public boolean checkMobile(String value, Long id) {
        return count(Wraps.<DefUser>lbQ().eq(DefUser::getMobile, value).ne(DefUser::getId, id)) > 0;
    }

    @Override
    public boolean checkIdCard(String value, Long id) {
        return count(Wraps.<DefUser>lbQ().eq(DefUser::getIdCard, value).ne(DefUser::getId, id)) > 0;
    }


    @Override
    public DefUser getUserByUsername(Integer loginType, String username) {
        CacheKey key = DefUserUserNameCacheKeyBuilder.builder(ToolsUtil.combineStrings(loginType.toString(), username));
        return getDefUser(key, loginType, username, DefUser::getUsername);
    }

    @Override
    public DefUser getUserByMobile(Integer loginType, String mobile) {
        CacheKey key = DefUserMobileCacheKeyBuilder.builder(ToolsUtil.combineStrings(loginType.toString(), mobile));
        return getDefUser(key, loginType, mobile, DefUser::getMobile);
    }

    @Override
    public DefUser getUserByEmail(Integer loginType, String email) {
        CacheKey key = DefUserEmailCacheKeyBuilder.builder(ToolsUtil.combineStrings(loginType.toString(), email));
        return getDefUser(key, loginType, email, DefUser::getEmail);
    }

    private DefUser getDefUser(CacheKey key, Integer loginType, String value, SFunction<DefUser, ?> fun) {
        CacheResult<Long> result = cacheOps.get(key, k -> {
            DefUser defUser = getOne(Wrappers.<DefUser>lambdaQuery().eq(DefUser::getSystemType, loginType).eq(fun, value), false);
            return defUser != null ? defUser.getId() : null;
        });
        return getByIdCache(result.getValue());
    }

    @Override
    public boolean removeById(DefUser entity) {
        delUserCache(Collections.singletonList(entity.getId()));
        return super.removeById(entity);
    }


    @Override
    public boolean removeByIds(Collection<?> list, boolean useFill) {
        delUserCache(list);
        return super.removeByIds(list, useFill);
    }

    @Override
    public boolean removeBatchByIds(Collection<?> list) {
        delUserCache(list);
        return super.removeBatchByIds(list);
    }


    @Override
    public void delUserCache(Collection<?> list) {
        if (CollUtil.isEmpty(list)) {
            return;
        }
        List<Long> idList = new ArrayList<>();
        for (Object o : list) {
            if (o instanceof DefUser user) {
                idList.add(user.getId());
            } else {
                idList.add(Convert.toLong(o));
            }
        }
        List<DefUser> defUsers = listByIds(idList);
        ArgumentAssert.notEmpty(defUsers, "待删除数据不存在");
        List<CacheKey> keyList = new ArrayList<>();
        for (DefUser defUser : defUsers) {
            CacheKey idCardKey = DefUserIdCardCacheKeyBuilder.builder(defUser.getIdCard());
            CacheKey mobileKey = DefUserMobileCacheKeyBuilder.builder(defUser.getMobile());
            CacheKey emailKey = DefUserEmailCacheKeyBuilder.builder(defUser.getEmail());
            CacheKey usernameKey = DefUserUserNameCacheKeyBuilder.builder(defUser.getUsername());
            keyList.add(idCardKey);
            keyList.add(mobileKey);
            keyList.add(emailKey);
            keyList.add(usernameKey);
        }

        cacheOps.del(keyList);
    }
}
