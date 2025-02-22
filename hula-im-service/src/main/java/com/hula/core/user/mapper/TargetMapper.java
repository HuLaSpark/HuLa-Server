package com.hula.core.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hula.core.user.domain.entity.Target;
import com.hula.core.chat.domain.vo.request.room.TargetVo;

import java.util.List;

public interface TargetMapper extends BaseMapper<Target> {

	List<TargetVo> getTargetList(List<Long> ids);
}
