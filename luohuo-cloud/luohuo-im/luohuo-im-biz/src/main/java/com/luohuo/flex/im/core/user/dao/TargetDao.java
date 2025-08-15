package com.luohuo.flex.im.core.user.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luohuo.flex.im.domain.entity.Target;
import com.luohuo.flex.im.domain.vo.request.room.TargetVo;
import com.luohuo.flex.im.core.user.mapper.TargetMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * <p>
 * 用户标签表 服务实现类
 * </p>
 *
 * @author 乾乾
 */
@Service
public class TargetDao extends ServiceImpl<TargetMapper, Target> {

	@Resource
	private TargetMapper targetMapper;

	public List<TargetVo> getTargetList(List<Long> ids) {
		return targetMapper.getTargetList(ids);
	}
}
