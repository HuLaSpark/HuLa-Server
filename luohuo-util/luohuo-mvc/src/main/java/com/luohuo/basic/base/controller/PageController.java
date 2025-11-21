package com.luohuo.basic.base.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.luohuo.basic.annotation.log.WebLog;
import com.luohuo.basic.base.R;
import com.luohuo.basic.base.entity.SuperEntity;
import com.luohuo.basic.base.request.PageParams;
import com.luohuo.basic.database.mybatis.conditions.Wraps;
import com.luohuo.basic.database.mybatis.conditions.query.QueryWrap;
import com.luohuo.basic.interfaces.echo.EchoService;
import com.luohuo.basic.utils.BeanPlusUtil;

import java.io.Serializable;

/**
 * 分页控制器
 *
 * @param <Entity>    实体
 * @param <PageQuery> 分页参数
 * @author 乾乾
 * @date 2020年03月07日22:06:35
 */
public interface PageController<Id extends Serializable, Entity extends SuperEntity<Id>, PageQuery, ResultVO>
        extends BaseController<Id, Entity> {

    /**
     * 获取返回VO的类型
     *
     * @return 实体的类型
     */
    Class<ResultVO> getResultVOClass();

    /**
     * 处理查询参数
     *
     * @param params 前端传递的参数
     * @author tangyh
     * @date 2021/7/3 3:25 下午
     * @create [2021/7/3 3:25 下午 ] [tangyh] [初始创建]
     */
    default void handlerQueryParams(PageParams<PageQuery> params) {
    }

    /**
     * 执行分页查询
     * <p>
     * 子类可以覆盖后重写查询逻辑
     *
     * @param params 分页参数
     * @return 分页信息
     */
    default IPage<Entity> query(PageParams<PageQuery> params) {
        // 处理查询参数，如：覆盖前端传递的 current、size、sort 等参数 以及 model 中的参数 【提供给子类重写】【无默认实现】
        handlerQueryParams(params);

        // 构建分页参数(current、size)和排序字段等
        IPage<Entity> page = params.buildPage(getEntityClass());
        Entity model = BeanUtil.toBean(params.getModel(), getEntityClass());

        // 根据前端传递的参数，构建查询条件【提供给子类重写】【有默认实现】
        QueryWrap<Entity> wrapper = handlerWrapper(model, params);

        // 执行单表分页查询
        getSuperService().page(page, wrapper);

        return page;
    }

    /**
     * 处理对象中的非空参数和扩展字段中的区间参数，可以覆盖后处理组装查询条件
     *
     * @param model  实体类
     * @param params 分页参数
     * @return 查询构造器
     */
    default QueryWrap<Entity> handlerWrapper(Entity model, PageParams<PageQuery> params) {
        return Wraps.q(model, params.getExtra(), getEntityClass());
    }

    /**
     * 获取echo Service
     *
     * @return 回显服务
     */
    default EchoService getEchoService() {
        return null;
    }

    /**
     * 处理查询后的数据
     * <p>
     * 如：执行@Echo回显
     *
     * @param page 分页对象
     */
    default void handlerResult(IPage<ResultVO> page) {
        EchoService echoService = getEchoService();
        if (echoService != null) {
            echoService.action(page);
        }
    }

    /**
     * 分页查询
     *
     * @param params 分页参数
     * @return 分页数据s
     */
    @Operation(summary = "分页列表查询")
    @PostMapping(value = "/page")
    @WebLog(value = "'分页列表查询:第' + #params?.current + '页, 显示' + #params?.size + '行'", response = false)
    default R<IPage<ResultVO>> page(@RequestBody @Validated PageParams<PageQuery> params) {
        IPage<Entity> page = query(params);
        IPage<ResultVO> voPage = BeanPlusUtil.toBeanPage(page, getResultVOClass());
        // 处理查询后的分页结果， 如：调用EchoService回显字典、关联表数据等 【提供给子类重写】【有默认实现】
        handlerResult(voPage);
        return success(voPage);
    }
}
