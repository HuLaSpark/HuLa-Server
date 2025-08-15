package com.luohuo.flex.ai.service.mindmap;

import com.luohuo.flex.ai.common.pojo.PageResult;
import com.luohuo.flex.ai.controller.mindmap.vo.AiMindMapGenerateReqVO;
import com.luohuo.flex.ai.controller.mindmap.vo.AiMindMapPageReqVO;
import com.luohuo.flex.ai.dal.mindmap.AiMindMapDO;
import com.luohuo.basic.base.R;
import reactor.core.publisher.Flux;

/**
 * AI 思维导图 Service 接口
 *
 * @author xiaoxin
 */
public interface AiMindMapService {

    /**
     * 生成思维导图内容
     *
     * @param generateReqVO 请求参数
     * @param userId        用户编号
     * @return 生成结果
     */
    Flux<R<String>> generateMindMap(AiMindMapGenerateReqVO generateReqVO, Long userId);

    /**
     * 删除思维导图
     *
     * @param id 编号
     */
    void deleteMindMap(Long id);

    /**
     * 获得思维导图分页
     *
     * @param pageReqVO 分页查询
     * @return 思维导图分页
     */
    PageResult<AiMindMapDO> getMindMapPage(AiMindMapPageReqVO pageReqVO);

}
