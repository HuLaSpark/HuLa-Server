package com.hula.core.user.dao;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hula.core.user.domain.entity.Black;
import com.hula.core.user.mapper.BlackMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 黑名单 服务实现类
 * </p>
 *
 * @author nyh
 */
@Service
public class BlackDao extends ServiceImpl<BlackMapper, Black> implements IService<Black> {

}
