package com.hula.ai.gpt.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hula.ai.common.utils.DozerUtil;
import com.hula.ai.gpt.mapper.AgreementMapper;
import com.hula.ai.gpt.pojo.command.AgreementCommand;
import com.hula.ai.gpt.pojo.entity.Agreement;
import com.hula.ai.gpt.pojo.param.AgreementParam;
import com.hula.ai.gpt.pojo.vo.AgreementVO;
import com.hula.ai.gpt.service.IAgreementService;
import com.hula.exception.BizException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 *  内容管理 服务实现类
 *
 * @author: 云裂痕
 * @date: 2023-04-28
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
@Service
public class AgreementServiceImpl extends ServiceImpl<AgreementMapper, Agreement> implements IAgreementService {
    @Autowired
    private AgreementMapper agreementMapper;

    /**
     * 根据id获取内容管理信息
     *
     * @param id 内容管理id
     * @return
     */
    private Agreement getContent(Long id) {
        Agreement agreement = agreementMapper.selectById(id);
        if (ObjectUtil.isNull(agreement)) {
            throw new BizException("内容管理信息不存在，无法操作");
        }
        return agreement;
    }

	public IPage<AgreementVO> pageContent(AgreementParam query) {
		return agreementMapper.pageContent(new Page<>(query.getCurrent(), query.getSize()), query);
	}



    @Override
    public List<AgreementVO> listContent(AgreementParam param) {
		return agreementMapper.listContent(param);
    }

    @Override
    public AgreementVO getContentById(Long id) {
        return DozerUtil.convertor(getContent(id), AgreementVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveContent(AgreementCommand command) {
        Agreement agreement = DozerUtil.convertor(command, Agreement.class);
        agreement.setCreatedBy(command.getOperater());
        agreementMapper.insert(agreement);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateContent(AgreementCommand command) {
        Agreement agreement = getContent(command.getId());
        DozerUtil.convertor(command, agreement);
        agreement.setUpdatedBy(command.getOperater());
        agreement.setUpdatedTime(LocalDateTime.now());
		return agreementMapper.updateById(agreement);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int removeContentByIds(List<Long> ids) {
        return agreementMapper.deleteBatchIds(ids);
    }
}
