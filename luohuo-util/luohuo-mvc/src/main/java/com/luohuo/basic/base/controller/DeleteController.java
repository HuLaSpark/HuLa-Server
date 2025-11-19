package com.luohuo.basic.base.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.luohuo.basic.annotation.log.WebLog;
import com.luohuo.basic.base.R;
import com.luohuo.basic.base.entity.SuperEntity;

import java.io.Serializable;
import java.util.List;


/**
 * 删除Controller
 *
 * @param <Entity> 实体
 * @param <Id>     主键
 * @author 乾乾
 * @date 2020年03月07日22:02:16
 */
public interface DeleteController<Id extends Serializable, Entity extends SuperEntity<Id>>
        extends BaseController<Id, Entity> {

    /**
     * 删除方法
     *
     * @param ids id
     * @return 是否成功
     */
    @Operation(summary = "删除")
    @DeleteMapping
    @WebLog("'删除:' + #ids")
    default R<Boolean> delete(@RequestBody List<Id> ids) {
        R<Boolean> result = handlerDelete(ids);
        if (result.getDefExec()) {
            return R.success(getSuperService().removeByIds(ids));
        }
        return result;
    }

    /**
     * 自定义删除
     *
     * @param ids id
     * @return 返回SUCCESS_RESPONSE, 调用默认更新, 返回其他不调用默认更新
     */
    default R<Boolean> handlerDelete(List<Id> ids) {
        return R.success();
    }

}
