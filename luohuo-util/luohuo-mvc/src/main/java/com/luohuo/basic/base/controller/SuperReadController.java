package com.luohuo.basic.base.controller;


import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.luohuo.basic.base.entity.SuperEntity;
import com.luohuo.basic.base.service.SuperService;

import java.io.Serializable;


/**
 * SuperReadController
 * <p>
 * 继承该类，就拥有了如下方法：
 * 1，page 分页查询，并支持子类扩展4个方法：handlerQueryParams、query、handlerWrapper、handlerResult
 * 2，get 单体查询， 根据ID直接查询DB
 * 3，detail 单体详情查询， 根据ID直接查询DB
 * 4，list 列表查询，根据参数条件，查询列表
 * <p>
 * 若重写扩展方法无法满足，则可以重写page、save等方法，但切记不要修改 @RequestMapping 参数
 *
 * @param <S>         Service
 * @param <Id>        主键
 * @param <Entity>    实体
 * @param <PageQuery> 分页参数
 * @param <ResultVO>  实体返回VO
 * @author 乾乾
 * @date 2020年03月06日11:06:46
 */
public abstract class SuperReadController<S extends SuperService<Id, Entity>, Id extends Serializable, Entity extends SuperEntity<Id>, PageQuery, ResultVO>
        extends SuperSimpleController<S, Id, Entity>
        implements QueryController<Id, Entity, PageQuery, ResultVO> {

    protected Class<ResultVO> resultVOClass = currentResultVOClass();

    protected Class<ResultVO> currentResultVOClass() {
        return (Class<ResultVO>) ReflectionKit.getSuperClassGenericType(this.getClass(), SuperReadController.class, 4);
    }

    @Override
    public Class<ResultVO> getResultVOClass() {
        return this.resultVOClass;
    }

}
