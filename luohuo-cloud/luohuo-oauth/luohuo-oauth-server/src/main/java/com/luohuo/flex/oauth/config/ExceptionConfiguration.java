package com.luohuo.flex.oauth.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.DispatcherServlet;
import com.luohuo.basic.boot.handler.AbstractGlobalExceptionHandler;

import jakarta.servlet.Servlet;

/**
 * 全局异常处理
 *
 * @author zuihou
 * @date 2020年01月02日17:19:27
 */
@Configuration
@ConditionalOnClass({Servlet.class, DispatcherServlet.class})
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@RestControllerAdvice(annotations = {RestController.class, Controller.class})
public class ExceptionConfiguration extends AbstractGlobalExceptionHandler {

}
