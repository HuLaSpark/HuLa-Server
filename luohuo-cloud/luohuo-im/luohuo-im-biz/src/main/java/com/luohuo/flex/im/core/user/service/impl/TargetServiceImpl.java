package com.luohuo.flex.im.core.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import com.luohuo.flex.im.domain.vo.request.room.TargetParam;
import com.luohuo.flex.im.domain.vo.request.room.TargetVo;
import com.luohuo.flex.im.core.user.dao.TargetDao;
import com.luohuo.flex.im.domain.entity.Target;
import com.luohuo.flex.im.core.user.service.TargetService;

import java.util.List;

@Service
public class TargetServiceImpl implements TargetService {

	@Resource
	private TargetDao targetDao;

	public List<TargetVo> getTargetList(List<Long> ids) {
		return targetDao.getTargetList(ids);
	}

	/**
	 * 添加好友标签
	 *
	 * @param uid
	 * @param param
	 * @return
	 */
	public Boolean save(Long uid, TargetParam param){
		Target target = new Target();
		target.setName(param.getName());
		target.setIcon(param.getIcon());
		target.setUid(uid);
		return targetDao.save(target);
	}

	/**
	 * 标签的修改
	 * @param param
	 * @return
	 */
	public Boolean edit(TargetParam param){
		Target target = BeanUtil.copyProperties(param, Target.class);
		target.setId(param.getId());
		return targetDao.updateById(target);
	}

	@Override
	public Target detail(Long id) {
		return targetDao.getById(id);
	}

	@Override
	public Boolean removeByIds(List<Long> ids) {
		return targetDao.removeByIds(ids);
	}
}
