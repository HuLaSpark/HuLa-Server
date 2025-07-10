package com.hula.ai.service.write;


import com.hula.ai.common.pojo.PageResult;
import com.hula.ai.controller.write.vo.AiWriteGenerateReqVO;
import com.hula.ai.controller.write.vo.AiWritePageReqVO;
import com.hula.ai.dal.write.AiWriteDO;
import com.hula.domain.vo.res.ApiResult;
import reactor.core.publisher.Flux;

/**
 * AI 写作 Service 接口
 *
 * @author xiaoxin
 */
public interface AiWriteService {

    /**
     * 生成写作内容
     *
     * @param generateReqVO 作文生成请求参数
     * @param userId        用户编号
     * @return 生成结果
     */
    Flux<ApiResult<String>> generateWriteContent(AiWriteGenerateReqVO generateReqVO, Long userId);

    /**
     * 删除写作
     *
     * @param id 编号
     */
    void deleteWrite(Long id);

    /**
     * 获得写作分页
     *
     * @param pageReqVO 分页查询
     * @return AI 写作分页
     */
    PageResult<AiWriteDO> getWritePage(AiWritePageReqVO pageReqVO);

}
