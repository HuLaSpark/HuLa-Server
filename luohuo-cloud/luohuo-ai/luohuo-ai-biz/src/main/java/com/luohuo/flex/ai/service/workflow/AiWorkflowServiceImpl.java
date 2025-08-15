package com.luohuo.flex.ai.service.workflow;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.luohuo.flex.ai.common.pojo.PageResult;
import com.luohuo.flex.ai.controller.workflow.vo.AiWorkflowPageReqVO;
import com.luohuo.flex.ai.controller.workflow.vo.AiWorkflowSaveReqVO;
import com.luohuo.flex.ai.controller.workflow.vo.AiWorkflowTestReqVO;
import com.luohuo.flex.ai.dal.workflow.AiWorkflowDO;
import com.luohuo.flex.ai.mapper.workflow.AiWorkflowMapper;
import com.luohuo.flex.ai.service.model.AiModelService;
import com.luohuo.flex.ai.utils.BeanUtils;
import dev.tinyflow.core.Tinyflow;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.luohuo.flex.ai.enums.ErrorCodeConstants.WORKFLOW_CODE_EXISTS;
import static com.luohuo.flex.ai.enums.ErrorCodeConstants.WORKFLOW_NOT_EXISTS;
import static com.luohuo.flex.ai.utils.ServiceExceptionUtil.exception;


/**
 * AI 工作流 Service 实现类
 *
 * @author lesan
 */
@Service
@Slf4j
public class AiWorkflowServiceImpl implements AiWorkflowService {

    @Resource
    private AiWorkflowMapper workflowMapper;

    @Resource
    private AiModelService apiModelService;

    @Override
    public Long createWorkflow(AiWorkflowSaveReqVO createReqVO) {
        // 1. 参数校验
        validateCodeUnique(null, createReqVO.getCode());

        // 2. 插入工作流配置
        AiWorkflowDO workflow = BeanUtils.toBean(createReqVO, AiWorkflowDO.class);
        workflowMapper.insert(workflow);
        return workflow.getId();
    }

    @Override
    public void updateWorkflow(AiWorkflowSaveReqVO updateReqVO) {
        // 1. 参数校验
        validateWorkflowExists(updateReqVO.getId());
        validateCodeUnique(updateReqVO.getId(), updateReqVO.getCode());

        // 2. 更新工作流配置
        AiWorkflowDO workflow = BeanUtils.toBean(updateReqVO, AiWorkflowDO.class);
        workflowMapper.updateById(workflow);
    }

    @Override
    public void deleteWorkflow(Long id) {
        // 1. 校验存在
        validateWorkflowExists(id);

        // 2. 删除工作流配置
        workflowMapper.deleteById(id);
    }

    private AiWorkflowDO validateWorkflowExists(Long id) {
        if (ObjUtil.isNull(id)) {
            throw exception(WORKFLOW_NOT_EXISTS);
        }
        AiWorkflowDO workflow = workflowMapper.selectById(id);
        if (ObjUtil.isNull(workflow)) {
            throw exception(WORKFLOW_NOT_EXISTS);
        }
        return workflow;
    }

    private void validateCodeUnique(Long id, String code) {
        if (StrUtil.isBlank(code)) {
            return;
        }
        AiWorkflowDO workflow = workflowMapper.selectByCode(code);
        if (ObjUtil.isNull(workflow)) {
            return;
        }
        if (ObjUtil.isNull(id)) {
            throw exception(WORKFLOW_CODE_EXISTS);
        }
        if (ObjUtil.notEqual(workflow.getId(), id)) {
            throw exception(WORKFLOW_CODE_EXISTS);
        }
    }

    @Override
    public AiWorkflowDO getWorkflow(Long id) {
        return workflowMapper.selectById(id);
    }

    @Override
    public PageResult<AiWorkflowDO> getWorkflowPage(AiWorkflowPageReqVO pageReqVO) {
        return workflowMapper.selectPage(pageReqVO);
    }

    @Override
    public Object testWorkflow(AiWorkflowTestReqVO testReqVO) {
        // 加载 graph
        String graph = testReqVO.getGraph() != null ? testReqVO.getGraph()
                : validateWorkflowExists(testReqVO.getId()).getGraph();

        // 构建 TinyFlow 执行链
        Tinyflow tinyflow = parseFlowParam(graph);

        // 执行
        Map<String, Object> variables = testReqVO.getParams();
        return tinyflow.toChain().executeForResult(variables);
    }

    private Tinyflow parseFlowParam(String graph) {
        // TODO @lesan：可以使用 jackson 哇？
        JSONObject json = JSONObject.parseObject(graph);
        JSONArray nodeArr = json.getJSONArray("nodes");
        Tinyflow tinyflow = new Tinyflow(json.toJSONString());
        for (int i = 0; i < nodeArr.size(); i++) {
            JSONObject node = nodeArr.getJSONObject(i);
            switch (node.getString("type")) {
                case "llmNode":
                    JSONObject data = node.getJSONObject("data");
                    apiModelService.getLLmProvider4Tinyflow(tinyflow, data.getLong("llmId"));
                    break;
                case "internalNode":
                    break;
                default:
                    break;
            }
        }
        return tinyflow;
    }

}
