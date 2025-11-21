package com.luohuo.flex.base.service.system.impl;

import cn.hutool.core.bean.BeanUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.luohuo.basic.base.service.impl.SuperServiceImpl;
import com.luohuo.basic.cache.repository.CachePlusOps;
import com.luohuo.basic.database.mybatis.conditions.Wraps;
import com.luohuo.basic.model.cache.CacheHashKey;
import com.luohuo.basic.utils.ArgumentAssert;
import com.luohuo.flex.base.entity.system.DefDict;
import com.luohuo.flex.base.manager.system.DefDictManager;
import com.luohuo.flex.base.vo.save.system.DefDictItemSaveVO;
import com.luohuo.flex.base.vo.update.system.DefDictItemUpdateVO;
import com.luohuo.flex.common.cache.tenant.base.DictCacheKeyBuilder;

import com.luohuo.flex.model.enumeration.system.DictClassifyEnum;
import com.luohuo.flex.base.service.system.DefDictItemService;

import java.util.Collection;

/**
 * <p>
 * 业务实现类
 * 字典
 * </p>
 *
 * @author 乾乾
 * @date 2021-10-04
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DefDictItemServiceImpl extends SuperServiceImpl<DefDictManager, Long, DefDict> implements DefDictItemService {

    private final CachePlusOps cachePlusOps;


    @Override
    public boolean checkItemByKey(String key, Long dictId, Long id) {
        ArgumentAssert.notEmpty(key, "请填写字典项标识");
        ArgumentAssert.notNull(dictId, "字典不能为空");
        return superManager.count(Wraps.<DefDict>lbQ().eq(DefDict::getKey, key)
                .eq(DefDict::getParentId, dictId).ne(DefDict::getId, id)) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public <SaveVO> DefDict save(SaveVO saveVO) {
        DefDictItemSaveVO itemSaveVO = (DefDictItemSaveVO) saveVO;
        DefDict model = BeanUtil.toBean(itemSaveVO, DefDict.class);
        ArgumentAssert.isFalse(checkItemByKey(model.getKey(), model.getParentId(), null), "字典[{}]已经存在，请勿重复创建", model.getKey());
        DefDict parent = getById(model.getParentId());
        ArgumentAssert.notNull(parent, "字典不存在");
        model.setParentKey(parent.getKey());
        model.setClassify(DictClassifyEnum.SYSTEM.getCode());
        superManager.save(model);
        CacheHashKey hashKey = DictCacheKeyBuilder.builder(model.getParentKey(), model.getKey());
        cachePlusOps.hSet(hashKey, model.getName());
        return model;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public <UpdateVO> DefDict updateById(UpdateVO updateVO) {
        DefDictItemUpdateVO itemUpdateVO = (DefDictItemUpdateVO) updateVO;
        DefDict model = BeanUtil.toBean(itemUpdateVO, DefDict.class);
        DefDict old = getById(model.getId());
        ArgumentAssert.notNull(old, "您要删除的字典项不存在或已被删除！");
        DefDict parent = getById(model.getParentId());
        ArgumentAssert.notNull(parent, "您要删除的字典不存在或已被删除！");
        model.setParentKey(parent.getKey());
        model.setClassify(DictClassifyEnum.SYSTEM.getCode());
        superManager.updateById(model);

        // 淘汰旧缓存
        CacheHashKey oldHashKey = DictCacheKeyBuilder.builder(parent.getKey(), old.getKey());
        cachePlusOps.hDel(oldHashKey);
        // 设置新缓存
        CacheHashKey hashKey = DictCacheKeyBuilder.builder(parent.getKey(), model.getKey());
        cachePlusOps.hSet(hashKey, model.getName());
        return model;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeByIds(Collection<Long> idList) {
        return superManager.removeItemByIds(idList);
    }
}
