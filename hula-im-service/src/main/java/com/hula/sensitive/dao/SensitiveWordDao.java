package com.hula.sensitive.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hula.sensitive.domain.SensitiveWord;
import com.hula.sensitive.mapper.SensitiveWordMapper;
import org.springframework.stereotype.Service;

/**
 * 敏感词DAO
 * @author nyh
 */
@Service
public class SensitiveWordDao extends ServiceImpl<SensitiveWordMapper, SensitiveWord> {

}
