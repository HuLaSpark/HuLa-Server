package com.hula.ai.gpt.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hula.ai.common.constant.StringPoolConstant;
import com.hula.ai.common.utils.DozerUtil;
import com.hula.ai.framework.util.CommonUtil;
import com.hula.ai.gpt.mapper.OpenkeyMapper;
import com.hula.ai.gpt.pojo.command.OpenkeyCommand;
import com.hula.ai.gpt.pojo.entity.Openkey;
import com.hula.ai.gpt.pojo.param.OpenKeyParam;
import com.hula.ai.gpt.pojo.vo.OpenkeyVO;
import com.hula.ai.gpt.service.IOpenkeyService;
import com.hula.ai.llm.base.key.factory.KeyUpdaterFactory;
import com.hula.ai.llm.base.key.updater.KeyUpdater;
import com.hula.exception.BizException;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 *  openai token 服务实现类
 *
 * @author: 云裂痕
 * @date: 2025-03-07
 * 得其道 乾乾
 */
@Service
public class OpenkeyServiceImpl extends ServiceImpl<OpenkeyMapper, Openkey> implements IOpenkeyService {
    @Resource
    private OpenkeyMapper openkeyMapper;

    /**
     * 根据id获取openai token信息
     *
     * @param id openai tokenid
     * @return
     */
    private Openkey getOpenkey(Long id) {
        Openkey openkey = openkeyMapper.selectById(id);
        if (ObjectUtil.isNull(openkey)) {
            throw new BizException("openai token信息不存在，无法操作");
        }
        return openkey;
    }

    @Override
    public IPage<OpenkeyVO> pageOpenkey(OpenKeyParam param) {
		IPage<OpenkeyVO> iPage = openkeyMapper.pageOpenkey(new Page<>(param.getCurrent(), param.getSize()), param);
		iPage.getRecords().stream().forEach(v -> {
			v.setAppKey(CommonUtil.passportEncrypt(v.getAppKey()));
			v.setAppSecret(CommonUtil.passportEncrypt(v.getAppSecret()));
		});
        return iPage;
    }


    @Override
    public List<OpenkeyVO> listOpenkey(OpenKeyParam param) {
		List<OpenkeyVO> openkeyVOS = openkeyMapper.listOpenkey(param);
		openkeyVOS.stream().forEach(v -> {
			v.setAppKey(CommonUtil.passportEncrypt(v.getAppKey()));
			v.setAppSecret(CommonUtil.passportEncrypt(v.getAppSecret()));
		});
        return openkeyVOS;
    }

    @Override
    public OpenkeyVO getOpenkeyById(Long id) {
        OpenkeyVO openkeyVO = DozerUtil.convertor(getOpenkey(id), OpenkeyVO.class);
        openkeyVO.setAppKey(CommonUtil.passportEncrypt(openkeyVO.getAppKey()));
        openkeyVO.setAppSecret(CommonUtil.passportEncrypt(openkeyVO.getAppSecret()));
        return openkeyVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int saveOpenkey(OpenkeyCommand command) {
        Openkey openkey = DozerUtil.convertor(command, Openkey.class);
        openkey.setCreatedBy(command.getOperater());
		return openkeyMapper.insert(openkey);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateOpenkey(OpenkeyCommand command) {
        Openkey openkey = getOpenkey(command.getId());
        openkey.setModel(command.getModel());
        openkey.setAppId(command.getAppId());
        openkey.setAppKey(ObjectUtil.isNotNull(command.getAppKey()) && !command.getAppKey().contains(StringPoolConstant.STAR) ? command.getAppKey() : openkey.getAppKey());
        openkey.setAppSecret(ObjectUtil.isNotNull(command.getAppSecret()) && !command.getAppSecret().contains(StringPoolConstant.STAR) ? command.getAppSecret() : openkey.getAppSecret());
        openkey.setTotalTokens(command.getTotalTokens());
        openkey.setRemark(command.getRemark());
        openkey.setStatus(command.getStatus());
        openkey.setUpdatedBy(command.getOperater());
        openkey.setUpdatedTime(LocalDateTime.now());
        openkeyMapper.updateById(openkey);
        // 修改内存中的key，解决无法马上生效问题
		updateKey(command.getModel(),command.getAppKey());
		return 1;
    }

    /**
     * 更新模型密钥
     * @param model
     * @param key
     */
    private void updateKey(String model,String key){
        KeyUpdater keyUpdater = SpringUtil.getBean(KeyUpdaterFactory.class).getKeyUpdater(model);
        keyUpdater.updateKey(key);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int removeOpenkeyByIds(List<Long> ids) {
		return openkeyMapper.deleteBatchIds(ids);
    }

	/**
	 * 查询模型的密钥
	 * @param model 模型
	 * @return
	 */
	public List<OpenkeyVO> listOpenkeyByModel(String model) {
		return openkeyMapper.selectList(
				new LambdaQueryWrapper<Openkey>()
						.eq(Openkey::getModel, model)
						.eq(Openkey::getStatus, 1)
		).stream().map(this::convertToVO).collect(Collectors.toList());
	}

	private OpenkeyVO convertToVO(Openkey entity) {
		OpenkeyVO vo = new OpenkeyVO();
		BeanUtils.copyProperties(entity, vo);
		return vo;
	}

	public void updateUsedTokens(String appKey, Integer usedTokens) {
		LambdaUpdateWrapper<Openkey> updateWrapper = new LambdaUpdateWrapper<>();
		updateWrapper.eq(Openkey::getAppKey, appKey)
				.setSql("used_tokens = used_tokens + " + usedTokens)
				.setSql("surplus_tokens = total_tokens - (used_tokens + " + usedTokens + ")");
		openkeyMapper.update(null, updateWrapper);
	}

}
