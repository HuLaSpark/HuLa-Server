package com.hula.ai.gpt.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hula.ai.common.utils.DozerUtil;
import com.hula.ai.gpt.mapper.AssistantTypeMapper;
import com.hula.ai.gpt.pojo.command.AssistantTypeCommand;
import com.hula.ai.gpt.pojo.entity.AssistantType;
import com.hula.ai.gpt.pojo.param.AgreementParam;
import com.hula.ai.gpt.pojo.param.AssustantTypeParams;
import com.hula.ai.gpt.pojo.vo.AssistantTypeVO;
import com.hula.ai.gpt.service.IAssistantTypeService;
import com.hula.exception.BizException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 *  助手分类 服务实现类
 *
 * @author: 云裂痕
 * @date: 2023-11-22
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
@Service
public class AssistantTypeServiceImpl extends ServiceImpl<AssistantTypeMapper, AssistantType> implements IAssistantTypeService {
    @Autowired
    private AssistantTypeMapper assistantTypeMapper;

    /**
     * 根据id获取助手分类信息
     *
     * @param id 助手分类id
     * @return
     */
    private AssistantType getAssistantType(Long id) {
        AssistantType assistantType = assistantTypeMapper.selectById(id);
        if (ObjectUtil.isNull(assistantType)) {
            throw new BizException("助手分类信息不存在，无法操作");
        }
        return assistantType;
    }

    @Override
    public IPage<AssistantTypeVO> pageAssistantType(AssustantTypeParams param) {
		return assistantTypeMapper.pageAssistantType(new Page<>(param.getCurrent(), param.getSize()), param);
    }

    @Override
    public List<AssistantTypeVO> listAssistantType(AgreementParam param) {
		return assistantTypeMapper.listAssistantType(param);
    }

    @Override
    public AssistantTypeVO getAssistantTypeById(Long id) {
        return DozerUtil.convertor(getAssistantType(id), AssistantTypeVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int saveAssistantType(AssistantTypeCommand command) {
        AssistantType assistantType = DozerUtil.convertor(command, AssistantType.class);
        assistantType.setCreatedBy(command.getOperater());
        return assistantTypeMapper.insert(assistantType);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateAssistantType(AssistantTypeCommand command) {
        AssistantType assistantType = getAssistantType(command.getId());
        DozerUtil.convertor(command, assistantType);
        assistantType.setUpdatedBy(command.getOperater());
        assistantType.setUpdatedTime(LocalDateTime.now());
        return assistantTypeMapper.updateById(assistantType);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int removeAssistantTypeByIds(List<Long> ids) {
		return assistantTypeMapper.deleteBatchIds(ids);
    }

}
