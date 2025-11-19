package com.luohuo.flex.ai.service.knowledge;

import cn.hutool.core.util.ObjUtil;
import com.luohuo.basic.utils.SpringUtils;
import com.luohuo.flex.ai.common.pojo.PageResult;
import com.luohuo.flex.ai.controller.knowledge.vo.knowledge.AiKnowledgePageReqVO;
import com.luohuo.flex.ai.controller.knowledge.vo.knowledge.AiKnowledgeSaveReqVO;
import com.luohuo.flex.ai.dal.knowledge.AiKnowledgeDO;
import com.luohuo.flex.ai.dal.model.AiModelDO;
import com.luohuo.flex.ai.mapper.knowledge.AiKnowledgeMapper;
import com.luohuo.flex.ai.service.model.AiModelService;
import com.luohuo.flex.ai.utils.BeanUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.luohuo.flex.ai.enums.ErrorCodeConstants.KNOWLEDGE_NOT_EXISTS;
import static com.luohuo.flex.ai.utils.ServiceExceptionUtil.exception;


/**
 * AI 知识库-基础信息 Service 实现类
 *
 * @author xiaoxin
 */
@Service
@Slf4j
public class AiKnowledgeServiceImpl implements AiKnowledgeService {

    @Resource
    private AiKnowledgeMapper knowledgeMapper;

    @Resource
    private AiModelService modelService;

    @Override
    public Long createKnowledge(AiKnowledgeSaveReqVO createReqVO) {
        // 1. 校验模型配置
        AiModelDO model = modelService.validateModel(createReqVO.getEmbeddingModelId());

        // 2. 插入知识库
        AiKnowledgeDO knowledge = BeanUtils.toBean(createReqVO, AiKnowledgeDO.class)
                .setEmbeddingModel(model.getModel());
        knowledgeMapper.insert(knowledge);
        return knowledge.getId();
    }

    @Override
    public void updateKnowledge(AiKnowledgeSaveReqVO updateReqVO) {
        // 1.1 校验知识库存在
        AiKnowledgeDO oldKnowledge = validateKnowledgeExists(updateReqVO.getId());
        // 1.2 校验模型配置
        AiModelDO model = modelService.validateModel(updateReqVO.getEmbeddingModelId());

        // 2. 更新知识库
        AiKnowledgeDO updateObj = BeanUtils.toBean(updateReqVO, AiKnowledgeDO.class)
                .setEmbeddingModel(model.getModel());
        knowledgeMapper.updateById(updateObj);

        // 3. 如果模型变化，需要 reindex 所有的文档
        if (ObjUtil.notEqual(oldKnowledge.getEmbeddingModelId(), updateReqVO.getEmbeddingModelId())) {
            SpringUtils.getBean(AiKnowledgeSegmentService.class).reindexByKnowledgeIdAsync(updateReqVO.getId());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteKnowledge(Long id) {
        // 1. 校验存在
        validateKnowledgeExists(id);

        // 2. 删除知识库下的所有文档及段落
        SpringUtils.getBean(AiKnowledgeDocumentService.class).deleteKnowledgeDocumentByKnowledgeId(id);

        // 3. 删除知识库
        // 特殊：知识库需要最后删除，不然相关的配置会找不到
        knowledgeMapper.deleteById(id);
    }

    @Override
    public AiKnowledgeDO getKnowledge(Long id) {
        return knowledgeMapper.selectById(id);
    }

    @Override
    public AiKnowledgeDO validateKnowledgeExists(Long id) {
        AiKnowledgeDO knowledge = knowledgeMapper.selectById(id);
        if (knowledge == null) {
            throw exception(KNOWLEDGE_NOT_EXISTS);
        }
        return knowledge;
    }

    @Override
    public PageResult<AiKnowledgeDO> getKnowledgePage(AiKnowledgePageReqVO pageReqVO) {
        return knowledgeMapper.selectPage(pageReqVO);
    }

    @Override
    public List<AiKnowledgeDO> getKnowledgeSimpleListByStatus(Integer status) {
        return knowledgeMapper.selectListByStatus(status);
    }

}
