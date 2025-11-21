package com.luohuo.basic.base.controller;

import cn.hutool.core.collection.CollUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import com.luohuo.basic.annotation.log.WebLog;
import com.luohuo.basic.base.R;
import com.luohuo.basic.base.entity.SuperEntity;
import com.luohuo.basic.database.mybatis.conditions.Wraps;
import com.luohuo.basic.database.mybatis.conditions.query.QueryWrap;
import com.luohuo.basic.interfaces.echo.EchoService;
import com.luohuo.basic.utils.BeanPlusUtil;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 查询Controller
 *
 * @param <Entity>    实体
 * @param <Id>        主键
 * @param <PageQuery> 分页参数
 * @param <ResultVO>  实体返回VO
 * @author 乾乾
 * @date 2020年03月07日22:06:35
 */
public interface QueryController<Id extends Serializable, Entity extends SuperEntity<Id>, PageQuery, ResultVO>
        extends PageController<Id, Entity, PageQuery, ResultVO> {

    /**
     * 单体查询
     *
     * @param id 主键id
     * @return 查询结果
     */
    @Parameters({
            @Parameter(name = "id", description = "主键", schema = @Schema(type = "long"), in = ParameterIn.PATH),
    })
    @Operation(summary = "单体查询", description = "单体查询")
    @GetMapping("/{id:[0-9]+}")
    @WebLog("'查询:' + #id")
    default R<ResultVO> get(@PathVariable Id id) {
        Entity entity = getSuperService().getById(id);
        return success(BeanPlusUtil.toBean(entity, getResultVOClass()));
    }

    /**
     * 查询详情
     *
     * @param id 主键id
     * @return 查询结果
     */
    @Operation(summary = "查询单体详情")
    @GetMapping("/detail")
    @WebLog("'查询:' + #id")
    default R<ResultVO> getDetail(@RequestParam("id") Id id) {
        Entity entity = getSuperService().getById(id);
        ResultVO resultVO = BeanPlusUtil.toBean(entity, getResultVOClass());
        EchoService echoService = getEchoService();
        if (echoService != null) {
            echoService.action(resultVO);
        }
        return success(resultVO);
    }

    /**
     * 批量查询
     *
     * @param data 批量查询
     * @return 查询结果
     */
    @Operation(summary = "批量查询", description = "批量查询")
    @PostMapping("/query")
    @WebLog("批量查询")
    default R<List<ResultVO>> query(@RequestBody PageQuery data) {
        Entity entity = BeanPlusUtil.toBean(data, getEntityClass());
        QueryWrap<Entity> wrapper = Wraps.q(entity);
        List<Entity> list = getSuperService().list(wrapper);
        return success(BeanPlusUtil.toBeanList(list, getResultVOClass()));
    }


    /**
     * 批量查询
     *
     * @param ids 批量查询
     * @return 查询结果
     */
    @Operation(summary = "根据Id批量查询", description = "根据Id批量查询")
    @PostMapping("/findByIds")
    @WebLog("根据Id批量查询")
    default R<List<ResultVO>> findByIds(@RequestBody List<Id> ids) {
        if (CollUtil.isEmpty(ids)) {
            return R.success(Collections.emptyList());
        }
        List<Entity> list = getSuperService().listByIds(ids);
        return success(BeanPlusUtil.toBeanList(list, getResultVOClass()));
    }
}
