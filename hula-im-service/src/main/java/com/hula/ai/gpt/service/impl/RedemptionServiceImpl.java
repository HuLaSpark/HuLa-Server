package com.hula.ai.gpt.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hula.ai.common.utils.DozerUtil;
import com.hula.ai.gpt.mapper.RedemptionMapper;
import com.hula.ai.gpt.pojo.command.RedemptionCommand;
import com.hula.ai.gpt.pojo.entity.Redemption;
import com.hula.ai.gpt.pojo.param.RedemptionParam;
import com.hula.ai.gpt.pojo.vo.RedemptionVO;
import com.hula.ai.gpt.service.IRedemptionService;
import com.hula.exception.BizException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 *  兑换码 服务实现类
 *
 * @author: 云裂痕
 * @date: 2023-04-28
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
@Service
public class RedemptionServiceImpl extends ServiceImpl<RedemptionMapper, Redemption> implements IRedemptionService {
    @Autowired
    private RedemptionMapper redemptionMapper;

    /**
     * 根据id获取兑换码信息
     *
     * @param id 兑换码id
     * @return
     */
    private Redemption getRedemption(Long id) {
        Redemption redemption = redemptionMapper.selectById(id);
        if (ObjectUtil.isNull(redemption)) {
            throw new BizException("兑换码信息不存在，无法操作");
        }
        return redemption;
    }

    @Override
    public IPage<RedemptionVO> pageRedemption(RedemptionParam param) {
		return redemptionMapper.pageRedemption(new Page<>(param.getCurrent(), param.getSize()), param);
    }

    @Override
    public List<RedemptionVO> listRedemption(RedemptionParam param) {
		return redemptionMapper.listRedemption(param);

	}

    @Override
    public RedemptionVO getRedemptionById(Long id) {
        return DozerUtil.convertor(getRedemption(id), RedemptionVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int saveRedemption(RedemptionCommand command) {
        Redemption redemption = DozerUtil.convertor(command, Redemption.class);
        redemption.setCreatedBy(command.getOperater());
		return redemptionMapper.insert(redemption);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateRedemption(RedemptionCommand command) {
        Redemption redemption = getRedemption(command.getId());
        DozerUtil.convertor(command, redemption);
        redemption.setUpdatedBy(command.getOperater());
        redemption.setUpdatedTime(LocalDateTime.now());
		return redemptionMapper.updateById(redemption);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int removeRedemptionByIds(List<Long> ids) {
		return redemptionMapper.deleteBatchIds(ids);
    }

}
