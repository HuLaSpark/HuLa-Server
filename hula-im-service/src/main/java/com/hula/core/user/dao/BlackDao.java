package com.hula.core.user.dao;

import com.hula.core.user.domain.entity.Black;
import com.hula.core.user.mapper.BlackMapper;
import com.hula.core.user.service.BlackService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 黑名单 服务实现类
 * </p>
 *
 * @author nyh
 * @since 2024-05-05
 */
@Service
public class BlackDao extends ServiceImpl<BlackMapper, Black> implements BlackService {

}
