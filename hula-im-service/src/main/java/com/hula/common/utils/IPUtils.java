package com.hula.common.utils;

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

	// 需要检查的代理头，按优先级排序
	private static final String[] HEADERS_TO_CHECK = {
			"X-Real-IP",
			"X-Forwarded-For",
			"Proxy-Client-IP",
			"WL-Proxy-Client-IP",
			"HTTP_CLIENT_IP",
			"HTTP_X_FORWARDED_FOR"
	};

	/**
	 * 从多级反向代理中获取第一个合法IP
	 */
	public static String getMultistageReverseProxyIp(String ip) {
		if (ip != null && ip.contains(",")) {
			String[] ips = ip.trim().split(",");
			for (String subIp : ips) {
				subIp = extractPureIp(subIp.trim());
				if (isValidIP(subIp) && !isUnknown(subIp)) {
					return subIp;
				}
			}
		}
		String pureIp = extractPureIp(ip);
		return isValidIP(pureIp) ? pureIp : null;
	}

	/**
	 * 去除端口号并提取纯IP
	 */
	private static String extractPureIp(String ip) {
		if (ip == null) return null;
		ip = ip.trim();

		// 处理IPv6带端口格式 [::1]:8080
		if (ip.startsWith("[") && ip.contains("]")) {
			int end = ip.indexOf(']');
			ip = ip.substring(1, end);
		} else {
			// 处理IPv4或普通格式
			int colonPos = ip.indexOf(':');
			if (colonPos > 0 && !ip.substring(0, colonPos).contains(":")) {
				ip = ip.substring(0, colonPos);
			}
		}
		return ip;
	}

	/**
	 * 检查字符串是否为"unknown"或空
	 */
	public static boolean isUnknown(String checkString) {
		return checkString == null || checkString.trim().isEmpty() || "unknown".equalsIgnoreCase(checkString);
	}

	/**
	 * 严格验证IP合法性
	 */
	public static boolean isValidIP(String ip) {
		if (ip == null || ip.isEmpty()) return false;
		ip = ip.trim();

		// 检查IPv4
		if (ip.contains(".")) {
			return isValidIPv4(ip);
		}
		// 检查IPv6
		else if (ip.contains(":")) {
			return isValidIPv6(ip);
		}
		return false;
	}

	private static boolean isValidIPv4(String ip) {
		String[] parts = ip.split("\\.");
		if (parts.length != 4) return false;

		try {
			for (String part : parts) {
				int num = Integer.parseInt(part);
				if (num < 0 || num > 255) return false;
			}
			return !ip.endsWith(".");
		} catch (NumberFormatException e) {
			return false;
		}
	}

	private static boolean isValidIPv6(String ip) {
		try {
			InetAddress.getByName(ip);
			return ip.contains(":") && (ip.split(":").length <= 8);
		} catch (UnknownHostException e) {
			return false;
		}
	}

	/**
	 * 获取客户端真实IP
	 */
	public static String getHostIp(HttpServletRequest request) {
		String ip = null;

		// 按优先级检查代理头
		for (String header : HEADERS_TO_CHECK) {
			String headerValue = request.getHeader(header);
			if (!isUnknown(headerValue)) {
				ip = getMultistageReverseProxyIp(headerValue);
				if (ip != null) break;
			}
		}

		// 直接获取远端地址并处理特殊IP
		if (isUnknown(ip)) {
			ip = request.getRemoteAddr();
			// 处理IPv6本地地址
			if ("0:0:0:0:0:0:0:1".equals(ip) || "::1".equals(ip)) {
				ip = "127.0.0.1";
			}
		}

		return isValidIP(ip) ? ip : null;
	}
}