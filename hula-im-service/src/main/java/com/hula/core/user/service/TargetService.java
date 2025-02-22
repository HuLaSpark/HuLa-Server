package com.hula.core.user.service;

import com.hula.core.user.domain.entity.Target;
import com.hula.core.chat.domain.vo.request.room.TargetParam;
import com.hula.core.chat.domain.vo.request.room.TargetVo;
import java.util.List;

public interface TargetService {

	List<TargetVo> getTargetList(List<Long> ids);

	/**
	 * 添加好友标签
	 *
	 * @param uid
	 * @param param
	 * @return
	 */
	Boolean save(Long uid, TargetParam param);

	/**
	 * 标签的修改
	 * @param param
	 * @return
	 */
	Boolean edit(TargetParam param);

	/**
	 * 查询标签详情
	 * @param id
	 * @return
	 */
	Target detail(Long id);

	/**
	 * 移除标签
	 * @return
	 */
	Boolean removeByIds(List<Long> ids);
}
