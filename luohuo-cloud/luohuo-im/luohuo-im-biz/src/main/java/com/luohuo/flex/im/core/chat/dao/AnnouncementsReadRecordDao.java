package com.luohuo.flex.im.core.chat.dao;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luohuo.flex.im.domain.entity.AnnouncementsReadRecord;
import com.luohuo.flex.im.core.chat.mapper.AnnouncementsReadRecordMapper;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 公告已读记录 服务实现类
 * </p>
 *
 * @author nyh
 */
@Service
public class AnnouncementsReadRecordDao extends ServiceImpl<AnnouncementsReadRecordMapper, AnnouncementsReadRecord> implements IService<AnnouncementsReadRecord> {

}
