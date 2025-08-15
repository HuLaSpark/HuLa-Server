package com.luohuo.flex.im.core.chat.dao;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import com.luohuo.flex.im.domain.entity.Announcements;
import com.luohuo.flex.im.core.chat.mapper.AnnouncementsMapper;


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
