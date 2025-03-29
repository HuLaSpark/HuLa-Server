package com.hula.ai.gpt.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hula.ai.common.utils.DozerUtil;
import com.hula.ai.gpt.mapper.CombMapper;
import com.hula.ai.gpt.pojo.command.CombCommand;
import com.hula.ai.gpt.pojo.entity.Comb;
import com.hula.ai.gpt.pojo.param.CombParam;
import com.hula.ai.gpt.pojo.vo.CombVO;
import com.hula.ai.gpt.service.ICombService;
import com.hula.exception.BizException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 *  会员套餐 服务实现类
 *
 * @author: 云裂痕
 * @date: 2025-03-08
 * @version: 1.2.8
 * 得其道 乾乾
 */
@Service
public class CombServiceImpl extends ServiceImpl<CombMapper, Comb> implements ICombService {
    @Autowired
    private CombMapper combMapper;

    /**
     * 根据id获取会员套餐信息
     *
     * @param id 会员套餐id
     * @return
     */
    private Comb getComb(Long id) {
        Comb comb = combMapper.selectById(id);
        if (ObjectUtil.isNull(comb)) {
            throw new BizException("会员套餐信息不存在，无法操作");
        }
        return comb;
    }

    @Override
    public IPage<CombVO> pageComb(CombParam param) {
		return combMapper.pageComb(new Page<>(param.getCurrent(), param.getSize()), param);
    }

    @Override
    public List<CombVO> listComb(CombParam param) {
		return combMapper.listComb(param);
	}

    @Override
    public CombVO getCombById(Long id) {
        return DozerUtil.convertor(getComb(id), CombVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int saveComb(CombCommand command) {
        Comb comb = DozerUtil.convertor(command, Comb.class);
        comb.setCreatedBy(command.getOperater());
		return combMapper.insert(comb);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, transactionManager = "masterTransactionManager")
    public int updateComb(CombCommand command) {
        Comb comb = getComb(command.getId());
        DozerUtil.convertor(command, comb);
        comb.setUpdatedBy(command.getOperater());
        comb.setUpdatedTime(LocalDateTime.now());
		return combMapper.updateById(comb);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int removeCombByIds(List<Long> ids) {
		return combMapper.deleteBatchIds(ids);
    }

}
