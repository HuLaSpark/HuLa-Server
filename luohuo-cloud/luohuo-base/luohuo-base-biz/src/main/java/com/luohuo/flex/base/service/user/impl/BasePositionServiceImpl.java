package com.luohuo.flex.base.service.user.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.luohuo.basic.base.service.impl.SuperServiceImpl;
import com.luohuo.basic.database.mybatis.conditions.Wraps;
import com.luohuo.basic.database.mybatis.conditions.query.LbQueryWrap;
import com.luohuo.basic.utils.ArgumentAssert;
import com.luohuo.flex.base.entity.user.BasePosition;
import com.luohuo.flex.base.manager.user.BasePositionManager;
import com.luohuo.flex.base.service.user.BasePositionService;
import com.luohuo.flex.base.vo.save.user.BasePositionSaveVO;
import com.luohuo.flex.base.vo.update.user.BasePositionUpdateVO;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 业务实现类
 * 岗位
 * </p>
 *
 * @author zuihou
 * @date 2021-10-18
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)

public class BasePositionServiceImpl extends SuperServiceImpl<BasePositionManager, Long, BasePosition> implements BasePositionService {

    @Override
    public Map<Serializable, Object> findByIds(Set<Serializable> ids) {
        return superManager.findByIds(ids.stream().map(Convert::toLong).collect(Collectors.toSet()));
    }

    @Override
    public boolean check(String name, Long orgId, Long id) {
        ArgumentAssert.notEmpty(name, "请填写名称");
        LbQueryWrap<BasePosition> wrap = Wraps.<BasePosition>lbQ().eq(BasePosition::getName, name).eq(BasePosition::getOrgId, orgId).ne(BasePosition::getId, id);
        return superManager.count(wrap) > 0;
    }

    @Override
    protected <SaveVO> BasePosition saveBefore(SaveVO saveVO) {
        BasePositionSaveVO positionSaveVO = (BasePositionSaveVO) saveVO;
        ArgumentAssert.isFalse(check(positionSaveVO.getName(), positionSaveVO.getOrgId(), null), StrUtil.format("岗位[{}]已经存在", positionSaveVO.getName()));
        return super.saveBefore(saveVO);
    }

    @Override
    protected <UpdateVO> BasePosition updateBefore(UpdateVO updateVO) {
        BasePositionUpdateVO positionUpdateVO = (BasePositionUpdateVO) updateVO;
        ArgumentAssert.isFalse(check(positionUpdateVO.getName(), positionUpdateVO.getOrgId(), positionUpdateVO.getId()), StrUtil.format("岗位[{}]已经存在", positionUpdateVO.getName()));
        return super.updateBefore(updateVO);
    }
}
