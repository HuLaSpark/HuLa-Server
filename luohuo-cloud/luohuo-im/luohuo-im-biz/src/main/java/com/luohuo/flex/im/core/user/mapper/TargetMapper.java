package com.luohuo.flex.im.core.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;
import com.luohuo.flex.im.domain.entity.Target;
import com.luohuo.flex.im.domain.vo.request.room.TargetVo;

import java.util.List;

@Repository
public interface TargetMapper extends BaseMapper<Target> {

	List<TargetVo> getTargetList(List<Long> ids);
}
