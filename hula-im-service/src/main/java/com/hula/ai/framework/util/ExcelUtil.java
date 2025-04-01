package com.hula.ai.framework.util;

import com.alibaba.fastjson.JSON;
import com.hula.domain.vo.res.ApiResult;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

/**
 * Excel工具类
 * 阿里巴巴Easy-Excel文档地址：https://easyexcel.opensource.alibaba.com/
 *
 * @author: 云裂痕
 * @date: 2023/01/31
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
public class ExcelUtil {

    /**
     * 生成excel流
     *
     * @param response
     * @param clazz
     * @param list
     * @param <U>
     */
    public static <U> void write(HttpServletResponse response, String fileName, Class<U> clazz, List<U> list) throws IOException {
        String sheetName = "sheet";
        try {
            response.setCharacterEncoding("utf-8");
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            fileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
            response.setHeader("fileName", fileName + ".xlsx");
//            EasyExcel.write(response.getOutputStream(), clazz).autoCloseStream(Boolean.FALSE).sheet(sheetName).doWrite(list);
        } catch (Exception e) {
            e.printStackTrace();
            // 重置response
            response.reset();
            response.setCharacterEncoding("utf-8");
            response.setContentType("application/json");
			ApiResult r = ApiResult.fail(-1, "下载文件失败");
            response.getWriter().println(JSON.toJSONString(r));
        }
    }

}
