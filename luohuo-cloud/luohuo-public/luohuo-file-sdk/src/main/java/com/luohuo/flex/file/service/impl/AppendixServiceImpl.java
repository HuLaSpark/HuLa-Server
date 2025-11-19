package com.luohuo.flex.file.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Multimap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.luohuo.basic.base.entity.SuperEntity;
import com.luohuo.basic.base.manager.impl.SuperManagerImpl;
import com.luohuo.basic.database.mybatis.conditions.Wraps;
import com.luohuo.basic.database.mybatis.conditions.query.LbQueryWrap;
import com.luohuo.basic.interfaces.echo.EchoVO;
import com.luohuo.basic.utils.ArgumentAssert;
import com.luohuo.basic.utils.BeanPlusUtil;
import com.luohuo.basic.utils.CollHelper;
import com.luohuo.flex.file.entity.Appendix;
import com.luohuo.flex.file.mapper.AppendixMapper;
import com.luohuo.flex.file.service.AppendixService;
import com.luohuo.flex.model.vo.result.AppendixResultVO;
import com.luohuo.flex.model.vo.save.AppendixSaveVO;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>
 * 业务实现类
 * 业务附件
 * </p>
 *
 * @author tangyh
 * @date 2021-06-30
 * @create [2021-06-30] [tangyh] [初始创建]
 */
@Slf4j
@Service
public class AppendixServiceImpl extends SuperManagerImpl<AppendixMapper, Appendix> implements AppendixService {


    @Override
    @Transactional(readOnly = true)
    public <T extends SuperEntity<Long> & EchoVO> void echoAppendix(IPage<T> page, String... bizTypes) {
        if (page == null) {
            return;
        }
        echoAppendix(page.getRecords(), bizTypes);
    }

    @Override
    public <T extends SuperEntity<Long> & EchoVO> void echoAppendix(List<T> list, String... bizTypes) {
        if (CollUtil.isEmpty(list)) {
            return;
        }
        List<Long> ids = list.stream().map(SuperEntity::getId).collect(Collectors.toList());

        Multimap<AppendixService.AppendixBizKey, AppendixResultVO> map = listByBizIds(ids, bizTypes);

        Set<String> bizTypeSet = CollUtil.newHashSet();
        map.forEach((biz, item) -> bizTypeSet.add(biz.getBizType()));

        list.forEach(item -> {
            bizTypeSet.forEach(bizType -> {
                Collection<AppendixResultVO> colls = map.get(buildBiz(item.getId(), bizType));
                item.getEchoMap().put(bizType, colls);
            });
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Multimap<AppendixService.AppendixBizKey, AppendixResultVO> listByBizId(Long bizId, String... bizType) {
        ArgumentAssert.notNull(bizId, "请传入业务id");
        LbQueryWrap<Appendix> wrap = Wraps.<Appendix>lbQ().eq(Appendix::getBizId, bizId).in(Appendix::getBizType, bizType);
        List<Appendix> list = list(wrap);
        return CollHelper.iterableToMultiMap(list, item -> AppendixService.AppendixBizKey.builder().bizId(item.getBizId()).bizType(item.getBizType()).build(), item -> BeanPlusUtil.toBean(item, AppendixResultVO.class));
    }

    @Override
    @Transactional(readOnly = true)
    public Multimap<AppendixService.AppendixBizKey, AppendixResultVO> listByBizIds(List<Long> bizIds, String... bizType) {
        ArgumentAssert.notEmpty(bizIds, "请传入业务id");
        LbQueryWrap<Appendix> wrap = Wraps.<Appendix>lbQ().in(Appendix::getBizId, bizIds).in(Appendix::getBizType, bizType);
        List<Appendix> list = list(wrap);
        return CollHelper.iterableToMultiMap(list, item -> AppendixService.AppendixBizKey.builder().bizId(item.getBizId()).bizType(item.getBizType()).build(), item -> BeanPlusUtil.toBean(item, AppendixResultVO.class));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppendixResultVO> listByBizIdAndBizType(Long bizId, String bizType) {
        ArgumentAssert.notNull(bizId, "请传入业务id");
        LbQueryWrap<Appendix> wrap = Wraps.<Appendix>lbQ().eq(Appendix::getBizId, bizId).eq(Appendix::getBizType, bizType);
        List<Appendix> list = baseMapper.selectList(wrap);
        return list.stream().map(item -> BeanUtil.toBean(item, AppendixResultVO.class)).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AppendixResultVO getByBiz(Long bizId, String bizType) {
        ArgumentAssert.notNull(bizId, "请传入业务id");
        ArgumentAssert.notEmpty(bizType, "请传入功能点");
        LbQueryWrap<Appendix> wrap = Wraps.<Appendix>lbQ().eq(Appendix::getBizId, bizId).eq(Appendix::getBizType, bizType);
        List<Appendix> list = list(wrap);
        if (CollUtil.isEmpty(list)) {
            return null;
        }
        Appendix appendix = list.get(0);
        return BeanPlusUtil.toBean(appendix, AppendixResultVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean save(AppendixSaveVO appendix) {
        return save(Collections.singletonList(appendix));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean save(List<AppendixSaveVO> appendixList) {
        if (CollUtil.isEmpty(appendixList)) {
            return true;
        }

        removeByBiz(appendixList);

        List<Appendix> allList = appendixList.stream().filter(Objects::nonNull)
                .flatMap(appendix -> Optional.ofNullable(appendix.getTypeFiles()).orElse(List.of())
                        .stream().filter(Objects::nonNull)
                        .flatMap(item -> CollUtil.isEmpty(item.getFileIdList()) || StrUtil.isEmpty(item.getBizType())
                                ? Stream.empty()
                                : item.getFileIdList().stream().filter(Objects::nonNull).map(id -> {
                            Appendix dix = new Appendix();
                            dix.setBizId(appendix.getBizId()).setBizType(item.getBizType());
                            dix.setId(id);
                            return dix;
                        }))).toList();

        return saveBatch(allList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeByBiz(List<AppendixSaveVO> appendixList) {
        if (CollUtil.isEmpty(appendixList)) {
            return;
        }

        for (AppendixSaveVO appendix : appendixList) {
            if (appendix.getBizId() == null || CollUtil.isEmpty(appendix.getTypeFiles())) {
                continue;
            }

            List<String> bizTypes = appendix.getTypeFiles().stream().filter(Objects::nonNull).map(AppendixSaveVO.TypeFile::getBizType).filter(StrUtil::isNotEmpty).toList();
            if (CollUtil.isEmpty(bizTypes)) {
                continue;
            }

            remove(Wraps.<Appendix>lbQ().eq(Appendix::getBizId, appendix.getBizId()).in(Appendix::getBizType, bizTypes));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeByBizId(Collection<Long> bizIds, String bizType) {
        if (CollUtil.isEmpty(bizIds) || StrUtil.isEmpty(bizType)) {
            return false;
        }
        return remove(Wraps.<Appendix>lbQ().eq(Appendix::getBizType, bizType).in(Appendix::getBizId, bizIds));
    }

}
