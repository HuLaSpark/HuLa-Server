package com.luohuo.basic.base.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import com.luohuo.basic.base.R;
import com.luohuo.basic.base.entity.SuperEntity;
import com.luohuo.basic.base.request.PageParams;
import com.luohuo.basic.base.service.SuperCacheService;
import com.luohuo.basic.utils.BeanPlusUtil;
import com.luohuo.basic.validator.utils.AssertUtil;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 导入导出
 *
 * @author 乾乾
 * @date 2020年03月06日11:06:46
 */
@Slf4j
public abstract class SuperExcelController<S extends SuperCacheService<Id, Entity>,
        Id extends Serializable, Entity extends SuperEntity<Id>, SaveVO, UpdateVO, PageQuery, ResultVO>
        extends SuperController<S, Id, Entity, SaveVO, UpdateVO, PageQuery, ResultVO> {
    @Override
    public SuperCacheService<Id, Entity> getSuperService() {
        return superService;
    }

    private static final String FILE_NAME = "filename";

    protected Class<SaveVO> saveVOClass = currentSaveVOClass();

    protected Class<SaveVO> currentSaveVOClass() {
        return (Class<SaveVO>) ReflectionKit.getSuperClassGenericType(this.getClass(), SuperExcelController.class, 3);
    }

    public Class<SaveVO> getSaveVOClass() {
        return this.saveVOClass;
    }

    public abstract Class<?> getExcelClass();

    /**
     * 导出Excel
     *
     * @param params   参数
     * @param request  请求
     * @param response 响应
     */
    @Operation(summary = "导出Excel", description = "导出Excel")
    @PostMapping(value = "/export", produces = "application/octet-stream")
    public void exportExcel(@RequestBody @Validated PageParams<PageQuery> params, HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String filename = (String) params.getExtra().getOrDefault(FILE_NAME, "导出");

            String fileName = URLEncoder.encode(filename, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");

            List<?> list = findExportList(params);
            EasyExcel.write(response.getOutputStream(), getExcelClass()).sheet("模板").doWrite(list);
        } catch (Exception e) {
            log.error("导出失败", e);
            response.reset();
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            R error = R.fail("下载文件失败");
            response.getWriter().println(JSONUtil.toJsonStr(error));
        }
    }

    /**
     * 查询待导出的数据， 子类可以重写
     *
     * @param params params
     * @return java.util.List
     * @date 2021/5/23 10:25 下午
     * @create [2021/5/23 10:25 下午 ] [tangyh] [初始创建]
     * @update [2021/5/23 10:25 下午 ] [tangyh] [变更描述]
     */
    public List<?> findExportList(PageParams<PageQuery> params) {
        params.setSize(params.getSize() == -1 ? Convert.toLong(Integer.MAX_VALUE) : params.getSize());
        IPage<Entity> page = query(params);
        return BeanPlusUtil.toBeanList(page.getRecords(), getExcelClass());
    }

    @Operation(summary = "导入Excel", description = "导入Excel")
    @PostMapping(value = "/import")
    public R<Boolean> importExcel(@RequestParam(value = "file") MultipartFile simpleFile,
                                  HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 配置excel第一行字段名
        try {
            List<SaveVO> dataList = EasyExcel.read(simpleFile.getInputStream()).head(getSaveVOClass()).sheet().doReadSync();

            String failMsg = AssertUtil.validateAllSneaky(dataList, 1);
            if (StrUtil.isNotEmpty(failMsg)) {
                return R.fail(failMsg);
            }

            // 业务校验
            List<Entity> list = validData(dataList);

            superService.saveBatch(list);

            return R.success();
        } catch (ExcelDataConvertException e) {
            log.error("导入数据格式错误", e);
            ExcelContentProperty excelContentProperty = e.getExcelContentProperty();
            Field field = excelContentProperty.getField();
            ExcelProperty excelProperty = field.getAnnotation(ExcelProperty.class);
            String name = "";
            if (excelProperty != null) {
                name = StrUtil.join(".", excelProperty.value());
            } else {
                Schema apiModelProperty = field.getAnnotation(Schema.class);
                if (apiModelProperty != null) {
                    name = apiModelProperty.description();
                } else {
                    name = field.getName();
                }
            }
            Integer rowIndex = e.getRowIndex();
            Integer columnIndex = e.getColumnIndex() + 1;
            String value = e.getCellData().getStringValue();
            String msg = "第{}行，第{}列，字段【{}】的参数值：【{}】 填写有误，请认真检查。";
            return R.fail(StrUtil.format(msg, rowIndex, columnIndex, name, value));
        }
    }

    /**
     * 子类实现业务校验规则，校验失败自行报错
     * @param dataList 读取的原始数据
     */
    protected List<Entity> validData(List<SaveVO> dataList) {
        // 业务校验

        // 数据转换
        return BeanUtil.copyToList(dataList, getEntityClass());
    }
}
