package com.luohuo.flex.file.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Multimap;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.lang.Nullable;
import com.luohuo.basic.base.entity.SuperEntity;
import com.luohuo.basic.base.manager.SuperManager;
import com.luohuo.basic.interfaces.echo.EchoVO;
import com.luohuo.flex.file.entity.Appendix;
import com.luohuo.flex.model.vo.result.AppendixResultVO;
import com.luohuo.flex.model.vo.save.AppendixSaveVO;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 业务接口
 * 业务附件
 * </p>
 *
 * @author tangyh
 * @date 2021-06-30
 * @create [2021-06-30] [tangyh] [初始创建]
 */
public interface AppendixService extends SuperManager<Appendix> {

    /**
     * 回显附件
     *
     * @param page     分页数据
     * @param bizTypes 业务类型
     * @author tangyh
     * @date 2021/8/28 3:53 下午
     * @create [2021/8/28 3:53 下午 ] [tangyh] [初始创建]
     */
    <T extends SuperEntity<Long> & EchoVO> void echoAppendix(IPage<T> page, String... bizTypes);

    /**
     * 回显附件
     *
     * @param list     列表数据
     * @param bizTypes 业务类型
     * @author tangyh
     * @date 2021/8/28 3:53 下午
     * @create [2021/8/28 3:53 下午 ] [tangyh] [初始创建]
     */
    <T extends SuperEntity<Long> & EchoVO> void echoAppendix(List<T> list, String... bizTypes);

    /**
     * 构建 listByObjectId 方法的key
     *
     * @param bizId   业务id
     * @param bizType 业务类型
     * @return 业务附件
     */
    default AppendixBizKey buildBiz(Long bizId, String bizType) {
        return AppendixBizKey.builder().bizId(bizId).bizType(bizType).build();
    }

    /**
     * 根据业务id 和 任意个业务类型 查询附件
     * <p>
     * 返回值为：
     * bizId + bizType -> [附件, ...]
     *
     * @param bizId   业务id
     * @param bizType 业务类型
     * @return 附件
     */
    Multimap<AppendixBizKey, AppendixResultVO> listByBizId(Long bizId, @Nullable String... bizType);

    /**
     * 根据业务id 和 任意个业务类型 查询附件
     * <p>
     * 返回值为：
     * bizId + bizType -> [附件, ...]
     *
     * @param bizIds  业务id
     * @param bizType 业务类型
     * @return 附件
     */
    Multimap<AppendixBizKey, AppendixResultVO> listByBizIds(List<Long> bizIds, @Nullable String... bizType);

    /**
     * 根据业务id 和 业务类型 查询附件
     * <p>
     * 返回值为： [附件, ...]
     *
     * @param bizId   业务id
     * @param bizType 业务类型
     * @return 附件
     */
    List<AppendixResultVO> listByBizIdAndBizType(Long bizId, String bizType);

    /**
     * 根据业务id 和 业务类型查询附件， 若查到多个附件，也只返回一个。
     * <p>
     * 请业务方自行确保该业务类型的附件始终只有一个。
     *
     * @param bizId 业务id
     * @param bizType 业务类型
     * @return 附件
     */
    AppendixResultVO getByBiz(Long bizId, String bizType);

    /**
     * 新增附件信息
     * <p>
     * 逻辑：
     * 1. bizId 不能为空，先根据bizId删除附件
     * 2. 若附件信息不为为空，保存最新的附件信息
     *
     * @param appendix  业务附件
     * @return 是否成功
     */
    Boolean save(AppendixSaveVO appendix);

    /**
     * 新增附件信息
     * <p>
     * 逻辑：
     * 1. bizId 不能为空，先根据bizId删除附件
     * 2. 若附件信息不为为空，保存最新的附件信息
     *
     * @param appendixList  业务附件
     * @return 是否成功
     */
    Boolean save(List<AppendixSaveVO> appendixList);

    /**
     * 根据业务id批量删除附件
     *
     * 适用于一张表只有1个附件时，批量删除业务数据
     *
     * @param bizType 业务类型
     * @param bizIds 业务id
     * @return 是否删除了记录
     */
    boolean removeByBizId(Collection<Long> bizIds, String bizType);

    /**
     * 删除多条数据
     *
     * 使用于一张表有多个附件时，同时删除多条数据的多个业务类型
     *
     * @param appendixList 附件
     * */
    void removeByBiz(List<AppendixSaveVO> appendixList);

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    class AppendixBizKey implements Serializable {
        private Long bizId;
        private String bizType;
    }
}
