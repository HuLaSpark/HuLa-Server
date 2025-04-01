package com.hula.ai.gpt.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hula.ai.gpt.pojo.entity.Agreement;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hula.ai.gpt.pojo.param.AgreementParam;
import com.hula.ai.gpt.pojo.vo.AgreementVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 内容管理 Mapper 接口
 *
 * @author: 云裂痕
 * @date: 2023-04-28
 * @version: 1.0.0
 * 得其道 乾乾
 */
public interface AgreementMapper extends BaseMapper<Agreement> {

	/**
	 * 分页查询内容管理列表
	 *
	 * @param page  分页参数
	 * @param query 查询条件
	 * @return
	 */
	IPage<AgreementVO> pageContent(IPage page, @Param("q") AgreementParam query);

	/**
	 * 查询内容管理列表
	 *
	 * @param param 查询条件
	 * @return
	 */
	List<AgreementVO> listContent(@Param("q") AgreementParam param);
}
