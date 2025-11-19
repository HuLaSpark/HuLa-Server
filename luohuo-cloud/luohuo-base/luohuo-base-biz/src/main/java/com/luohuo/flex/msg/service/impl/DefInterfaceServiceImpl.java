package com.luohuo.flex.msg.service.impl;

import cn.hutool.core.util.StrUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.luohuo.basic.base.service.impl.SuperServiceImpl;
import com.luohuo.basic.database.mybatis.conditions.Wraps;
import com.luohuo.basic.utils.ArgumentAssert;

import com.luohuo.flex.msg.entity.DefInterface;
import com.luohuo.flex.msg.entity.DefInterfaceProperty;
import com.luohuo.flex.msg.enumeration.InterfaceExecModeEnum;
import com.luohuo.flex.msg.manager.DefInterfaceManager;
import com.luohuo.flex.msg.manager.DefInterfacePropertyManager;
import com.luohuo.flex.msg.service.DefInterfaceService;
import com.luohuo.flex.msg.vo.save.DefInterfaceSaveVO;
import com.luohuo.flex.msg.vo.update.DefInterfaceUpdateVO;

import java.util.Collection;

/**
 * <p>
 * 业务实现类
 * 接口
 * </p>
 *
 * @author 乾乾
 * @date 2022-07-04 16:45:45
 * @create [2022-07-04 16:45:45] [zuihou] [代码生成器生成]
 */

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class DefInterfaceServiceImpl extends SuperServiceImpl<DefInterfaceManager, Long, DefInterface> implements DefInterfaceService {
    private final DefInterfacePropertyManager defInterfacePropertyManager;

    @Override
    public Boolean check(String code, Long id) {
        ArgumentAssert.notEmpty(code, "请填写接口编码");
        return superManager.count(Wraps.<DefInterface>lbQ().eq(DefInterface::getCode, code)
                .ne(DefInterface::getId, id)) > 0;
    }

    @Override
    protected <SaveVO> DefInterface saveBefore(SaveVO saveVO) {
        DefInterfaceSaveVO interfaceSaveVO = (DefInterfaceSaveVO) saveVO;
        ArgumentAssert.isFalse(StrUtil.isNotBlank(interfaceSaveVO.getCode()) &&
                check(interfaceSaveVO.getCode(), null), "接口编码{}已存在", interfaceSaveVO.getCode());
        if (InterfaceExecModeEnum.IMPL_CLASS.eq(interfaceSaveVO.getExecMode())) {
            ArgumentAssert.notEmpty(interfaceSaveVO.getImplClass(), "请填写实现类");
        } else {
            ArgumentAssert.notEmpty(interfaceSaveVO.getScript(), "请填写实现脚本");
        }
        return super.saveBefore(saveVO);
    }

    @Override
    protected <UpdateVO> DefInterface updateBefore(UpdateVO updateVO) {
        DefInterfaceUpdateVO interfaceUpdateVO = (DefInterfaceUpdateVO) updateVO;
        ArgumentAssert.isFalse(StrUtil.isNotBlank(interfaceUpdateVO.getCode()) &&
                        check(interfaceUpdateVO.getCode(), interfaceUpdateVO.getId()),
                "接口编码{}已存在", interfaceUpdateVO.getCode());
        if (InterfaceExecModeEnum.IMPL_CLASS.eq(interfaceUpdateVO.getExecMode())) {
            ArgumentAssert.notEmpty(interfaceUpdateVO.getImplClass(), "请填写实现类");
        } else {
            ArgumentAssert.notEmpty(interfaceUpdateVO.getScript(), "请填写实现脚本");
        }
        return super.updateBefore(interfaceUpdateVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeByIds(Collection<Long> idList) {
        defInterfacePropertyManager.remove(Wraps.<DefInterfaceProperty>lbQ().in(DefInterfaceProperty::getInterfaceId, idList));
        return super.removeByIds(idList);
    }
}


