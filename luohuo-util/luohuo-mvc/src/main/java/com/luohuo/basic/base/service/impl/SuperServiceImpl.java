package com.luohuo.basic.base.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import com.luohuo.basic.base.entity.SuperEntity;
import com.luohuo.basic.base.manager.SuperManager;
import com.luohuo.basic.base.service.SuperService;
import com.luohuo.basic.utils.ArgumentAssert;
import com.luohuo.basic.utils.BeanPlusUtil;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * 不含缓存的Service实现
 * <p>
 * 2，removeById：重写 ServiceImpl 类的方法，删除db
 * 3，removeByIds：重写 ServiceImpl 类的方法，删除db
 * 4，updateAllById： 新增的方法： 修改数据（所有字段）
 * 5，updateById：重写 ServiceImpl 类的方法，修改db后
 *
 * @param <M>      Manager
 * @param <Id>     ID
 * @param <Entity> 实体
 * @author 乾乾
 * @date 2020年02月27日18:15:17
 */
public class SuperServiceImpl<M extends SuperManager<Entity>, Id extends Serializable, Entity extends SuperEntity<?>> implements SuperService<Id, Entity> {
    @Autowired
    protected M superManager;

    protected Class<M> managerClass = currentManagerClass();
    protected Class<Entity> entityClass = currentModelClass();
    protected Class<Id> idClass = currentIdClass();

    @Override
    public M getSuperManager() {
        return superManager;
    }

    @Override
    public Class<Entity> getEntityClass() {
        return entityClass;
    }

    @Override
    public Class<Id> getIdClass() {
        return idClass;
    }

    protected Class<M> currentManagerClass() {
        return (Class<M>) ReflectionKit.getSuperClassGenericType(this.getClass(), SuperServiceImpl.class, 0);
    }

    protected Class<Id> currentIdClass() {
        return (Class<Id>) ReflectionKit.getSuperClassGenericType(this.getClass(), SuperServiceImpl.class, 1);
    }

    protected Class<Entity> currentModelClass() {
        return (Class<Entity>) ReflectionKit.getSuperClassGenericType(this.getClass(), SuperServiceImpl.class, 2);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public <SaveVO> Entity save(SaveVO saveVO) {
        Entity entity = saveBefore(saveVO);
        this.getSuperManager().save(entity);
        saveAfter(saveVO, entity);
        return entity;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveBatch(List<Entity> entityList) {
        return this.getSuperManager().saveBatch(entityList);
    }

    /**
     * 保存之前处理参数等操作
     *
     * @param saveVO 保存VO
     */
    protected <SaveVO> Entity saveBefore(SaveVO saveVO) {
        return BeanUtil.toBean(saveVO, getEntityClass());
    }

    /**
     * 保存之后设置参数值，淘汰缓存等操作
     *
     * @param saveVO 保存VO
     * @param entity 实体
     */
    protected <SaveVO> void saveAfter(SaveVO saveVO, Entity entity) {
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public <UpdateVO> Entity updateById(UpdateVO updateVO) {
        Entity entity = updateBefore(updateVO);
        getSuperManager().updateById(entity);
        updateAfter(updateVO, entity);
        return entity;
    }

    /**
     * 修改之前处理参数等操作
     *
     * @param updateVO 修改VO
     */
    protected <UpdateVO> Entity updateBefore(UpdateVO updateVO) {
        return BeanUtil.toBean(updateVO, getEntityClass());
    }

    /**
     * 修改之后设置参数值，淘汰缓存等操作
     *
     * @param updateVO 修改VO
     * @param entity   实体
     */
    protected <UpdateVO> void updateAfter(UpdateVO updateVO, Entity entity) {
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public <UpdateVO> Entity updateAllById(UpdateVO updateVO) {
        Entity entity = updateAllBefore(updateVO);
        getSuperManager().updateAllById(entity);
        updateAllAfter(updateVO, entity);
        return entity;
    }

    /**
     * 修改之前处理参数等操作
     *
     * @param updateVO 修改VO
     */
    protected <UpdateVO> Entity updateAllBefore(UpdateVO updateVO) {
        return BeanUtil.toBean(updateVO, getEntityClass());
    }

    /**
     * 修改之后设置参数值，淘汰缓存等操作
     *
     * @param updateVO 修改VO
     * @param entity   实体
     */
    protected <UpdateVO> void updateAllAfter(UpdateVO updateVO, Entity entity) {
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Entity copy(Id id) {
        Entity old = getById(id);
        ArgumentAssert.notNull(old, "您要复制的数据不存在或已被删除，请刷新重试");
        Entity entity = BeanPlusUtil.toBean(old, getEntityClass());
        entity.setId(null);
        superManager.save(entity);
        return entity;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeByIds(Collection<Id> idList) {
        return getSuperManager().removeByIds(idList);
    }

    @Override
    @Transactional(readOnly = true)
    public Entity getById(Id id) {
        return getSuperManager().getById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Entity> list(Wrapper<Entity> queryWrapper) {
        return getSuperManager().list(queryWrapper);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Entity> listByIds(List<Id> ids) {
        return getSuperManager().listByIds(ids);
    }

    @Override
    @Transactional(readOnly = true)
    public <E extends IPage<Entity>> E page(E page, Wrapper<Entity> queryWrapper) {
        return getSuperManager().page(page, queryWrapper);
    }
}
