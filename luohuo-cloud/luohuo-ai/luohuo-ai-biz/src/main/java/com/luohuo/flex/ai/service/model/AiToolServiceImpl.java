package com.luohuo.flex.ai.service.model;

import cn.hutool.extra.spring.SpringUtil;
import com.luohuo.flex.ai.common.pojo.PageResult;
import com.luohuo.flex.ai.controller.model.vo.tool.AiToolPageReqVO;
import com.luohuo.flex.ai.controller.model.vo.tool.AiToolSaveReqVO;
import com.luohuo.flex.ai.dal.model.AiToolDO;
import com.luohuo.flex.ai.mapper.model.AiToolMapper;
import com.luohuo.flex.ai.utils.BeanUtils;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;
import java.util.List;

import static com.luohuo.flex.ai.enums.ErrorCodeConstants.TOOL_NAME_NOT_EXISTS;
import static com.luohuo.flex.ai.enums.ErrorCodeConstants.TOOL_NOT_EXISTS;
import static com.luohuo.flex.ai.utils.ServiceExceptionUtil.exception;


/**
 * AI 工具 Service 实现类
 *
 * @author 乾乾
 */
@Service
@Validated
public class AiToolServiceImpl implements AiToolService {

    @Resource
    private AiToolMapper toolMapper;

    @Override
    public Long createTool(AiToolSaveReqVO createReqVO) {
        // 校验名称是否存在
        validateToolNameExists(createReqVO.getName());

        // 插入
        AiToolDO tool = BeanUtils.toBean(createReqVO, AiToolDO.class);
        toolMapper.insert(tool);
        return tool.getId();
    }

    @Override
    public void updateTool(AiToolSaveReqVO updateReqVO) {
        // 1.1 校验存在
        validateToolExists(updateReqVO.getId());
        // 1.2 校验名称是否存在
        validateToolNameExists(updateReqVO.getName());

        // 2. 更新
        AiToolDO updateObj = BeanUtils.toBean(updateReqVO, AiToolDO.class);
        toolMapper.updateById(updateObj);
    }

    @Override
    public void deleteTool(Long id) {
        // 校验存在
        validateToolExists(id);
        // 删除
        toolMapper.deleteById(id);
    }

    @Override
    public void validateToolExists(Long id) {
        if (toolMapper.selectById(id) == null) {
            throw exception(TOOL_NOT_EXISTS);
        }
    }

    private void validateToolNameExists(String name) {
        try {
            SpringUtil.getBean(name);
        } catch (NoSuchBeanDefinitionException e) {
            throw exception(TOOL_NAME_NOT_EXISTS, name);
        }
    }

    @Override
    public AiToolDO getTool(Long id) {
        return toolMapper.selectById(id);
    }

    @Override
    public List<AiToolDO> getToolList(Collection<Long> ids) {
        return toolMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<AiToolDO> getToolPage(AiToolPageReqVO pageReqVO) {
        return toolMapper.selectPage(pageReqVO);
    }

    @Override
    public List<AiToolDO> getToolListByStatus(Integer status) {
        return toolMapper.selectListByStatus(status);
    }

}