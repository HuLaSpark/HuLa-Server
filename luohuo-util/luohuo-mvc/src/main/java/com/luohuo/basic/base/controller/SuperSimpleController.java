package com.luohuo.basic.base.controller;

import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import org.springframework.beans.factory.annotation.Autowired;
import com.luohuo.basic.base.entity.SuperEntity;
import com.luohuo.basic.base.service.SuperService;

import java.io.Serializable;

/**
 * 简单的实现了BaseController，为了获取注入 Service 和 实体类型
 * <p>
 * 基类该类后，没有任何方法。
 * 可以让业务Controller继承 SuperSimpleController 后，按需实现 *Controller 接口
 *
 * @param <S>      Service
 * @param <Entity> 实体
 * @author 乾乾
 * @date 2020年03月07日22:08:27
 */
public abstract class SuperSimpleController<S extends SuperService<Id, Entity>, Id extends Serializable, Entity extends SuperEntity<Id>>
        implements BaseController<Id, Entity> {
    @Autowired
    protected S superService;
    protected Class<Entity> entityClass = currentModelClass();

    protected Class<Entity> currentModelClass() {
        return (Class<Entity>) ReflectionKit.getSuperClassGenericType(this.getClass(), SuperSimpleController.class, 2);
    }

    @Override
    public Class<Entity> getEntityClass() {
        return this.entityClass;
    }

    @Override
    public SuperService<Id, Entity> getSuperService() {
        return superService;
    }


}
