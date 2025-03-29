package com.hula.ai.gpt.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hula.ai.common.utils.DozerUtil;
import com.hula.ai.gpt.mapper.AssistantMapper;
import com.hula.ai.gpt.pojo.command.AssistantCommand;
import com.hula.ai.gpt.pojo.entity.Assistant;
import com.hula.ai.gpt.pojo.param.AgreementParam;
import com.hula.ai.gpt.pojo.param.AssustantParams;
import com.hula.ai.gpt.pojo.vo.AppAssistantVO;
import com.hula.ai.gpt.pojo.vo.AssistantVO;
import com.hula.ai.gpt.service.IAssistantService;
import com.hula.domain.vo.res.ApiResult;
import com.hula.exception.BizException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * AI助理功能 服务实现类
 *
 * @author: 云裂痕
 * @date: 2023-04-28
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
@Service
public class AssistantServiceImpl extends ServiceImpl<AssistantMapper, Assistant> implements IAssistantService {
    @Autowired
    private AssistantMapper assistantMapper;

    /**
     * 根据id获取AI助理功能信息
     *
     * @param id AI助理功能id
     * @return
     */
    private Assistant getAssistant(Long id) {
        Assistant assistant = assistantMapper.selectById(id);
        if (ObjectUtil.isNull(assistant)) {
            throw new BizException("AI助理功能信息不存在，无法操作");
        }
        return assistant;
    }

    @Override
    public IPage<AssistantVO> pageAssistant(AssustantParams param) {
		return assistantMapper.pageAssistant(new Page<>(param.getCurrent(), param.getSize()), param);
    }

    @Override
    public List<AssistantVO> listAssistant(AssustantParams param) {
		return assistantMapper.listAssistant(param);
    }

    @Override
    public List<AppAssistantVO> listAssistantByApp(AssustantParams param) {
        List<AssistantVO> assistants;
		if (ObjectUtil.isNotNull(param.getSize()) && ObjectUtil.isNotNull(param.getCurrent())) {
			assistants = assistantMapper.pageAssistant(new Page(param.getCurrent(), param.getSize()), param).getRecords();
		} else {
			assistants = assistantMapper.listAssistantRandom(param);
		}

        return DozerUtil.convertor(assistants, AppAssistantVO.class);
    }

    @Override
    public ApiResult<AssistantVO> getAssistantById(Long id) {
        return ApiResult.success(DozerUtil.convertor(getAssistant(id), AssistantVO.class));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResult saveAssistant(AssistantCommand command) {
        Assistant assistant = DozerUtil.convertor(command, Assistant.class);
        assistant.setCreatedBy(command.getOperater());
        assistantMapper.insert(assistant);
        return ApiResult.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResult updateAssistant(AssistantCommand command) {
        Assistant assistant = getAssistant(command.getId());
        DozerUtil.convertor(command, assistant);
        assistant.setCreatedBy(command.getOperater());
        assistant.setUpdatedTime(LocalDateTime.now());
        assistantMapper.updateById(assistant);
        return ApiResult.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResult removeAssistantByIds(List<Long> ids) {
        assistantMapper.deleteBatchIds(ids);
        return ApiResult.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResult removeAssistantById(Long id) {
        assistantMapper.deleteById(id);
        return ApiResult.success();
    }

}
