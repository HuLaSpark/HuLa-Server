package com.luohuo.basic.base.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.luohuo.basic.base.entity.SuperEntity;
import com.luohuo.basic.base.manager.SuperManager;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * 业务层
 *
 * @param <Id>     ID
 * @param <Entity> 实体
 * @author 乾乾
 * @date 2020年03月03日20:49:03
 */
public interface SuperService<Id extends Serializable, Entity extends SuperEntity<?>> {

    /**
     * 获取实体的类型
     *
     * @return 实体类class类型
     */
    Class<Entity> getEntityClass();

    /**
     * 获取主键的类型
     *
     * @return 主键class类型
     */
    Class<Id> getIdClass();

    /**
     * 获取Manager的类型
     *
     * @return Manager的class类型
     */
    SuperManager<Entity> getSuperManager();

    /**
     * 插入一条记录（选择字段，策略插入）
     *
     * @param entity 实体对象 [返回的对象才有id, 传入的vo没有id]
     * @return 是否插入成功
     */
    <SaveVO> Entity save(SaveVO entity);

    /**
     * 批量保存
     *
     * @param saveList 实体集合
     * @return 是否执行成功
     */
    boolean saveBatch(List<Entity> saveList);

    /**
     * 复制一条数据
     * <p>
     * 注意：若该数据存在唯一索引等限制条件，需要重写该方法进行判断或处理。
     *
     * @param id ID
     * @return 复制后的实体
     */
    Entity copy(Id id);

    /**
     * 根据 ID 修改实体中非空的字段
     *
     * @param entity 实体对象
     * @return 是否修改成功
     */
    <UpdateVO> Entity updateById(UpdateVO entity);

    /**
     * 根据id修改 entity 的所有字段
     *
     * @param entity 实体对象
     * @return 是否修改成功
     */
    <UpdateVO> Entity updateAllById(UpdateVO entity);

    /**
     * 删除（根据ID 批量删除）
     *
     * @param idList 主键ID列表
     * @return 是否删除成功
     */
    boolean removeByIds(Collection<Id> idList);

    /**
     * 根据 ID 查询
     *
     * @param id 主键ID
     * @return 实体对象或null
     */
    Entity getById(Id id);

    /**
     * 查询列表
     *
     * @param queryWrapper 实体对象封装操作类 {@link com.baomidou.mybatisplus.core.conditions.query.QueryWrapper}
     * @return 实体对象集合或空集合
     */
    List<Entity> list(Wrapper<Entity> queryWrapper);

    /**
     * 批量查询
     *
     * @param ids 主键
     * @return 实体对象集合或空集合
     */
    List<Entity> listByIds(List<Id> ids);


    /**
     * 翻页查询
     *
     * @param page         翻页对象
     * @param queryWrapper 实体对象封装操作类 {@link com.baomidou.mybatisplus.core.conditions.query.QueryWrapper}
     * @return 实体分页对象
     */
    <E extends IPage<Entity>> E page(E page, Wrapper<Entity> queryWrapper);
}
