package com.luohuo.flex.im.core.user.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import com.luohuo.flex.im.common.enums.YesOrNoEnum;
import com.luohuo.flex.im.domain.entity.UserBackpack;
import com.luohuo.flex.im.core.user.mapper.UserBackpackMapper;

import java.util.List;

/**
 * <p>
 * 用户背包表 服务实现类
 * </p>
 *
 * @author nyh
 */
@Service
public class UserBackpackDao extends ServiceImpl<UserBackpackMapper, UserBackpack> {
	private final ItemConfigDao itemConfigDao;

	public UserBackpackDao(ItemConfigDao itemConfigDao) {
		this.itemConfigDao = itemConfigDao;
	}

	public Integer getCountByValidItemId(Long uid, Long itemId) {
		return Math.toIntExact(lambdaQuery().eq(UserBackpack::getUid, uid)
				.eq(UserBackpack::getItemId, itemId)
				.eq(UserBackpack::getStatus, YesOrNoEnum.NO.getStatus())
				.count());
	}

	public UserBackpack getFirstValidItem(Long uid, Long itemId) {
		LambdaQueryWrapper<UserBackpack> wrapper = new QueryWrapper<UserBackpack>().lambda()
				.eq(UserBackpack::getUid, uid)
				.eq(UserBackpack::getItemId, itemId)
				.eq(UserBackpack::getStatus, YesOrNoEnum.NO.getStatus())
				.last("limit 1");
		return getOne(wrapper);
	}

	public boolean invalidItem(Long id) {
		UserBackpack update = new UserBackpack();
		update.setId(id);
		update.setStatus(YesOrNoEnum.YES.getStatus());
		return updateById(update);
	}

	public List<UserBackpack> getByItemIds(Long uid, List<Long> itemIds) {
		return lambdaQuery().eq(UserBackpack::getUid, uid)
				.in(UserBackpack::getItemId, itemIds)
				.eq(UserBackpack::getStatus, YesOrNoEnum.NO.getStatus())
				.list();
	}

	public List<UserBackpack> getByItemIds(List<Long> uids, List<Long> itemIds) {
		return lambdaQuery().in(UserBackpack::getUid, uids)
				.in(UserBackpack::getItemId, itemIds)
				.eq(UserBackpack::getStatus, YesOrNoEnum.NO.getStatus())
				.list();
	}

	public UserBackpack getByIdp(String idempotent) {
		return lambdaQuery().eq(UserBackpack::getIdempotent, idempotent).one();
	}

	public long countByUidAndItemId(Long uid, Long itemId) {
		return baseMapper.selectCount(new LambdaQueryWrapper<UserBackpack>()
				.eq(UserBackpack::getUid, uid)
				.eq(UserBackpack::getItemId, itemId)
		);
	}
}
