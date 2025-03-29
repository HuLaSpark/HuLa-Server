package com.hula.ai.gpt.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hula.ai.gpt.pojo.command.AgreementCommand;
import com.hula.ai.gpt.pojo.entity.Agreement;
import com.hula.ai.gpt.pojo.param.AgreementParam;
import com.hula.ai.gpt.pojo.vo.AgreementVO;

import java.util.List;

/**
 * 内容管理 服务类
 *
 * @author: 云裂痕
 * @date: 2023-04-28
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
public interface IAgreementService extends IService<Agreement> {

    /**
     * 查询内容管理分页列表
     *
     * @param param 查询条件
     * @return 内容管理集合
     */
	IPage<AgreementVO> pageContent(AgreementParam param);

    /**
     * 查询内容管理列表
     *
     * @param param 查询条件
     * @return 内容管理集合
     */
	List<AgreementVO> listContent(AgreementParam param);

    /**
     * 根据主键查询内容管理
     *
     * @param id 内容管理主键
     * @return 内容管理
     */
	AgreementVO getContentById(Long id);

    /**
     * 新增内容管理
     *
     * @param command 内容管理
     * @return 结果
     */
	void saveContent(AgreementCommand command);

    /**
     * 修改内容管理
     *
     * @param command 内容管理
     * @return 结果
     */
    int updateContent(AgreementCommand command);

    /**
     * 批量删除内容管理
     *
     * @param ids 需要删除的内容管理主键集合
     * @return 结果
     */
    int removeContentByIds(List<Long> ids);

}
