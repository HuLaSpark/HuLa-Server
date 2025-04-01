package com.hula.ai.gpt.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hula.ai.gpt.pojo.entity.Comb;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hula.ai.gpt.pojo.param.CombParam;
import com.hula.ai.gpt.pojo.vo.CombVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 会员套餐 Mapper 接口
 *
 * @author: 云裂痕
 * @date: 2025-03-07
 * 得其道 乾乾
 */
public interface CombMapper extends BaseMapper<Comb> {

	/**
	 * 分页查询会员套餐列表
	 *
	 * @param page  分页参数
	 * @param query 查询条件
	 * @return
	 */
	IPage<CombVO> pageComb(IPage page, @Param("q") CombParam query);

	/**
	 * 查询会员套餐列表
	 *
	 * @param query 查询条件
	 * @return
	 */
	List<CombVO> listComb(@Param("q") CombParam query);
}
