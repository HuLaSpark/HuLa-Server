//package com.luohuo.flex.config;
//
//import jakarta.servlet.MultipartConfigElement;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.web.servlet.MultipartConfigFactory;
//import org.springframework.context.annotation.Bean;
//import org.springframework.stereotype.Component;
//import org.springframework.util.unit.DataSize;
//import com.luohuo.flex.service.SysConfigService;
//
///**
// * 文件的大小动态控制 生效配置需要引用当前的配置
// * @date 2024-04-18
// */
//@Component
//public class MultipartSizeConfiguration {
//
//	@Autowired
//	private SysConfigService configService;
//
//	@Bean
//	public MultipartConfigElement multipartConfigElement() {
//		MultipartConfigFactory factory = new MultipartConfigFactory();
//
//		Long multipartFileSize;
//		Long multipartRequestSize;
//
//		// 当输入错误的时候，默认设置为 512MB
//		try{
//			multipartFileSize = Long.parseLong(configService.get("multipart_file_size"));
//			multipartRequestSize = Long.parseLong(configService.get("multipart_request_size"));
//		}catch (NumberFormatException e){
//			multipartFileSize = 512L;
//			multipartRequestSize = 512L;
//		}
//
//		factory.setMaxFileSize(DataSize.ofMegabytes(multipartFileSize));
//		factory.setMaxRequestSize(DataSize.ofMegabytes(multipartRequestSize));
//		return factory.createMultipartConfig();
//	}
//
//}
