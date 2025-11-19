package com.luohuo.flex.im.core.chat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.luohuo.flex.model.entity.ws.ChatMember;
import com.luohuo.flex.model.entity.ws.ChatMemberResp;
import org.springframework.stereotype.Repository;
import com.luohuo.flex.im.domain.entity.GroupMember;

import java.util.List;

/**
 * <p>
 * 群成员表 Mapper 接口
 * </p>
 *
 * @author nyh
 */
@Repository
public interface GroupMemberMapper extends BaseMapper<GroupMember> {

	List<ChatMemberResp> getMemberListByGroupId(Long groupId);

	List<ChatMember> getMemberListByUid(List<Long> memberList);
}
