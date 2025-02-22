package com.hula.core.chat.dao;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hula.core.chat.domain.entity.Announcements;
import com.hula.core.chat.mapper.AnnouncementsMapper;
import org.springframework.stereotype.Service;


/**
 * <p>
 * 公告 服务实现类
 * </p>
 *
 * @author nyh
 */
@Service
public class AnnouncementsDao extends ServiceImpl<AnnouncementsMapper, Announcements> implements IService<Announcements> {

}
