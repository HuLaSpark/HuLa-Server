///*
// * Copyright 2020-2099 sa-token.cc
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package com.luohuo.flex.satoken.spring;
//
//import cn.dev33.satoken.SaManager;
//import cn.dev33.satoken.application.ApplicationInfo;
//import cn.dev33.satoken.context.model.SaRequest;
//import cn.dev33.satoken.exception.SaTokenException;
//import cn.dev33.satoken.servlet.error.SaServletErrorCode;
//import cn.dev33.satoken.util.SaFoxUtil;
//import cn.hutool.core.util.StrUtil;
//import cn.hutool.core.util.URLUtil;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.Cookie;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Enumeration;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * 对 SaRequest 包装类的实现（Jakarta-Servlet 版）
// *
// * @author click33
// * @since 1.34.0
// */
//public class MySaRequestForServlet implements SaRequest {
//
//    /**
//     * 底层Request对象
//     */
//    protected HttpServletRequest request;
//
//    /**
//     * 实例化
//     * @param request request对象
//     */
//    public MySaRequestForServlet(HttpServletRequest request) {
//        this.request = request;
//    }
//
//    /**
//     * 获取底层源对象
//     */
//    @Override
//    public Object getSource() {
//        return request;
//    }
//
//    /**
//     * 在 [请求体] 里获取一个值
//     */
//    @Override
//    public String getParam(String name) {
//        return request.getParameter(name);
//    }
//
//    /**
//     * 获取 [请求体] 里提交的所有参数名称
//     * @return 参数名称列表
//     */
//    @Override
//    public List<String> getParamNames() {
//        Enumeration<String> parameterNames = request.getParameterNames();
//        List<String> list = new ArrayList<>();
//        while (parameterNames.hasMoreElements()) {
//            list.add(parameterNames.nextElement());
//        }
//        return list;
//    }
//
//    /**
//     * 获取 [请求体] 里提交的所有参数
//     * @return 参数列表
//     */
//    @Override
//    public Map<String, String> getParamMap() {
//        // 获取所有参数
//        Map<String, String[]> parameterMap = request.getParameterMap();
//        Map<String, String> map = new LinkedHashMap<>(parameterMap.size());
//        for (String key : parameterMap.keySet()) {
//            String[] values = parameterMap.get(key);
//            map.put(key, values[0]);
//        }
//        return map;
//    }
//
//    /**
//     * 在 [请求头] 里获取一个值
//     */
//    @Override
//    public String getHeader(String name) {
//        String value = request.getHeader(name);
//        return StrUtil.isNotEmpty(value) ? URLUtil.decode(value) : value;
//    }
//
//    /**
//     * 在 [Cookie作用域] 里获取一个值
//     */
//    @Override
//    public String getCookieValue(String name) {
//        return getCookieLastValue(name);
//    }
//
//    /**
//     * 在 [ Cookie作用域 ] 里获取一个值 (第一个此名称的)
//     */
//    @Override
//    public String getCookieFirstValue(String name) {
//        Cookie[] cookies = request.getCookies();
//        if (cookies != null) {
//            for (Cookie cookie : cookies) {
//                if (cookie != null && name.equals(cookie.getName())) {
//                    return cookie.getValue();
//                }
//            }
//        }
//        return null;
//    }
//
//    /**
//     * 在 [ Cookie作用域 ] 里获取一个值 (最后一个此名称的)
//     * @param name 键
//     * @return 值
//     */
//    @Override
//    public String getCookieLastValue(String name) {
//        String value = null;
//        Cookie[] cookies = request.getCookies();
//        if (cookies != null) {
//            for (Cookie cookie : cookies) {
//                if (cookie != null && name.equals(cookie.getName())) {
//                    value = cookie.getValue();
//                }
//            }
//        }
//        return value;
//    }
//
//    /**
//     * 返回当前请求path (不包括上下文名称)
//     */
//    @Override
//    public String getRequestPath() {
//        return ApplicationInfo.cutPathPrefix(request.getRequestURI());
//    }
//
//    /**
//     * 返回当前请求的url，例：http://xxx.com/test
//     * @return see note
//     */
//    @Override
//    public String getUrl() {
//        String currDomain = SaManager.getConfig().getCurrDomain();
//        if (!SaFoxUtil.isEmpty(currDomain)) {
//            return currDomain + this.getRequestPath();
//        }
//        return request.getRequestURL().toString();
//    }
//
//    /**
//     * 返回当前请求的类型
//     */
//    @Override
//    public String getMethod() {
//        return request.getMethod();
//    }
//
//    /**
//     * 转发请求
//     */
//    @Override
//    public Object forward(String path) {
//        try {
//            HttpServletResponse response = (HttpServletResponse) SaManager.getSaTokenContextOrSecond().getResponse().getSource();
//            request.getRequestDispatcher(path).forward(request, response);
//            return null;
//        } catch (ServletException | IOException e) {
//            throw new SaTokenException(e).setCode(SaServletErrorCode.CODE_20001);
//        }
//    }
//
//}
