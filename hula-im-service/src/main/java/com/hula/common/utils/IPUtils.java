package com.hula.common.utils;

import cn.hutool.core.util.StrUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * IP 工具类
 *
 * @author 乾乾
 */
@Slf4j
public class IPUtils {

	/**
	 * 获取本地 IP 地址
	 *
	 * @return 本地 IP 地址
	 */
	public static String getHostIp() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
		}
		return "127.0.0.1";
	}

	/**
	 * 从多级反向代理中获得第一个非 unknown IP 地址
	 *
	 * @param ip 获得的 IP 地址
	 * @return 第一个非 unknown IP 地址
	 */
	public static String getMultistageReverseProxyIp(String ip) {
		// 多级反向代理检测
		if (ip != null && ip.indexOf(",") > 0) {
			final String[] ips = ip.trim().split(",");
			for (String subIp : ips) {
				if (false == isUnknown(subIp)) {
					ip = subIp;
					break;
				}
			}
		}
		return ip;
	}

	/**
	 * 检测给定字符串是否为未知，多用于检测 HTTP 请求相关
	 *
	 * @param checkString 被检测的字符串
	 * @return 是否未知
	 */
	public static boolean isUnknown(String checkString) {
		return StrUtil.isBlank(checkString) || "unknown".equalsIgnoreCase(checkString);
	}

	/**
	 * 从 HttpServletRequest 中获取用户请求的 IP 地址
	 *
	 * @param request HttpServletRequest 对象
	 * @return 用户请求的 IP 地址
	 */
	public static String getClientIp(HttpServletRequest request) {
		String ip = null;
		String[] headers = {
				"X-Forwarded-For",
				"Proxy-Client-IP",
				"WL-Proxy-Client-IP",
				"HTTP_CLIENT_IP",
				"HTTP_X_FORWARDED_FOR"
		};
		for (String header : headers) {
			ip = request.getHeader(header);
			if (!isUnknown(ip)) {
				ip = getMultistageReverseProxyIp(ip);
				break;
			}
		}
		if (isUnknown(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}
}