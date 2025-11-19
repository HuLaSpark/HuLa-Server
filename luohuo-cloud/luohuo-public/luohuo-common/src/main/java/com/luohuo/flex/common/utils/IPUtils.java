package com.luohuo.flex.common.utils;

import org.springframework.http.server.reactive.ServerHttpRequest;
import lombok.extern.slf4j.Slf4j;
import java.net.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * IP 工具类 - 增强版 支持 Spring Cloud Gateway
 * 支持从非浏览器客户端（如ApiFox）获取IP
 *
 * @author 乾乾
 */
@Slf4j
public class IPUtils {

	// 需要检查的代理头，按优先级排序
	private static final String[] HEADERS_TO_CHECK = {
			"X-Client-IP",
			"X-Real-IP",
			"CF-Connecting-IP",         // Cloudflare
			"True-Client-IP",           // Akamai
			"X-Original-Forwarded-For", // Kubernetes
			"X-Forwarded-For",
			"Proxy-Client-IP",
			"WL-Proxy-Client-IP",
			"HTTP_CLIENT_IP",
			"HTTP_X_FORWARDED_FOR"
	};

	// 私有IP范围CIDR（支持IPv4/IPv6）
	private static final List<String> PRIVATE_CIDRS = Arrays.asList(
			"10.0.0.0/8",
			"172.16.0.0/12",
			"192.168.0.0/16",
			"fd00::/8",
			"::1/128"
	);

	// IP格式预编译正则
	private static final Pattern IPv4_PATTERN = Pattern.compile(
			"^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$"
	);
	private static final Pattern IPv6_PATTERN = Pattern.compile(
			"^([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$|^::([0-9a-fA-F]{1,4}:){0,6}[0-9a-fA-F]{1,4}$|^[0-9a-fA-F]{1,4}::([0-9a-fA-F]{1,4}:){0,5}[0-9a-fA-F]{1,4}$"
	);

	// 可信代理列表（支持CIDR格式）
	private static final List<String> TRUSTED_PROXIES = Arrays.asList(
			"10.0.0.0/8",      // 内部网络
			"172.16.0.0/12",
			"192.168.0.0/16"
			// "127.0.0.1/32"  // 注释掉本地回环地址，避免测试环境误判
	);

	private static boolean isValidHeaderValue(String value) {
		return value != null &&
				!value.trim().isEmpty() &&
				!"unknown".equalsIgnoreCase(value);
	}

	/**
	 * 提取无端口IP（增强IPv6处理）
	 */
	private static String extractPureIP(String ipWithPort) {
		if (ipWithPort == null) return null;
		String trimmed = ipWithPort.trim();

		// 处理IPv6带端口格式 [::1]:8080
		if (trimmed.startsWith("[") && trimmed.contains("]")) {
			int endIndex = trimmed.indexOf(']');
			return trimmed.substring(1, endIndex);
		}

		// 处理IPv4或简单IPv6
		int colonIndex = trimmed.indexOf(':');
		if (colonIndex > 0 && !trimmed.substring(0, colonIndex).contains(":")) {
			return trimmed.substring(0, colonIndex);
		}

		return trimmed;
	}

	/**
	 * 增强IP格式验证
	 */
	private static boolean isValidIPFormat(String ip) {
		if (ip == null || ip.isEmpty()) return false;
		return IPv4_PATTERN.matcher(ip).matches() ||
				IPv6_PATTERN.matcher(ip).matches();
	}

	/**
	 * 检查是否为私有IP
	 */
	private static boolean isPrivateIP(String ip) {
		try {
			InetAddress address = InetAddress.getByName(ip);
			return PRIVATE_CIDRS.stream()
					.anyMatch(cidr -> {
						try {
							return new IPWithMask(cidr).contains(address);
						} catch (UnknownHostException e) {
							log.error("CIDR解析错误: {}", cidr, e);
							return false;
						}
					});
		} catch (UnknownHostException e) {
			log.error("IP地址解析错误: {}", ip, e);
			return false;
		}
	}

	/**
	 * 检查是否为可信代理IP
	 */
	private static boolean isTrustedProxy(String ip) {
		try {
			InetAddress address = InetAddress.getByName(ip);
			return TRUSTED_PROXIES.stream()
					.anyMatch(cidr -> {
						try {
							return new IPWithMask(cidr).contains(address);
						} catch (UnknownHostException e) {
							log.error("CIDR解析错误: {}", cidr, e);
							return false;
						}
					});
		} catch (UnknownHostException e) {
			log.error("IP地址解析错误: {}", ip, e);
			return false;
		}
	}

	/**
	 * 解析代理头值
	 */
	private static String parseProxyHeader(String headerValue) {
		if (headerValue == null || headerValue.isEmpty()) return null;

		// 处理多个IP的情况
		if (headerValue.contains(",")) {
			List<String> ips = Arrays.stream(headerValue.split(","))
					.map(String::trim)
					.map(IPUtils::extractPureIP)
					.filter(IPUtils::isValidIPFormat)
					.collect(Collectors.toList());

			// 优先选择首个非可信代理的公网IP
			for (String candidate : ips) {
				if (!isTrustedProxy(candidate) && !isPrivateIP(candidate)) {
					return candidate;
				}
			}

			// 如果没有非可信公网IP，则选择第一个非可信代理IP
			for (String candidate : ips) {
				if (!isTrustedProxy(candidate)) {
					return candidate;
				}
			}

			// 如果所有IP都是可信代理，则返回最后一个（最接近服务器的代理）
			return ips.isEmpty() ? null : ips.get(ips.size() - 1);
		}

		// 单个IP情况
		String pureIP = extractPureIP(headerValue);
		return isValidIPFormat(pureIP) ? pureIP : null;
	}

	/**
	 * 标准化特殊地址
	 */
	private static String normalizeSpecialIPs(String ip) {
		if (ip == null) return null;

		// IPv6本地地址标准化
		if ("0:0:0:0:0:0:0:1".equals(ip) || "::1".equals(ip)) {
			return "127.0.0.1";
		}
		// 处理IPv4兼容格式
		return ip.replaceAll("^::ffff:(\\d+\\.\\d+\\.\\d+\\.\\d+)$", "$1");
	}

	/**
	 * 验证是否为有效公网IP
	 */
	private static boolean isValidPublicIP(String ip) {
		return isValidIPFormat(ip) && !isPrivateIP(ip);
	}

	private static boolean isInvalidIP(String ip) {
		return ip == null ||
				!isValidIPFormat(ip);
		// 不再过滤127.0.0.1，允许本地测试
		// || "127.0.0.1".equals(ip);
	}

	/**
	 * 从 ServerWebExchange 获取客户端IP
	 */
	public static String getClientIp(ServerHttpRequest request) {
		log.debug("开始获取客户端IP，请求URI: {}", request.getURI());

		// 记录所有请求头用于调试
		log.trace("请求头: {}", request.getHeaders());

		String ip = null;

		// 1. 检查代理头
		for (String header : HEADERS_TO_CHECK) {
			String headerValue = request.getHeaders().getFirst(header);
			if (isValidHeaderValue(headerValue)) {
				ip = parseProxyHeader(headerValue);
				if (ip != null) {
					log.info("从代理头 [{}] 获取到IP: {}", header, ip);
					break;
				}
			}
		}

		// 2. 回退到远端地址
		if (isInvalidIP(ip)) {
			InetSocketAddress remoteAddress = request.getRemoteAddress();
			if (remoteAddress != null) {
				ip = remoteAddress.getAddress().getHostAddress();
				log.info("从远端地址获取到IP: {}", ip);
			} else {
				// 尝试从Host头获取（备选方案）
				String hostHeader = request.getHeaders().getFirst("Host");
				if (hostHeader != null && hostHeader.contains(":")) {
					ip = hostHeader.split(":")[0];
					log.warn("无法获取远端地址，从Host头获取IP: {}", ip);
				} else {
					log.error("无法获取客户端IP，使用默认值");
					ip = "0.0.0.0";
				}
			}
		}

		// 3. 处理特殊地址
		ip = normalizeSpecialIPs(ip);

		// 4. 最终验证（允许私有IP，因为可能是内部调用）
		return ip != null ? ip : "0.0.0.0";
	}

	/**
	 * CIDR处理辅助类
	 */
	private static class IPWithMask {
		private final InetAddress inetAddress;
		private final int mask;

		IPWithMask(String cidr) throws UnknownHostException {
			String[] parts = cidr.split("/");
			this.inetAddress = InetAddress.getByName(parts[0]);
			this.mask = (parts.length > 1) ? Integer.parseInt(parts[1]) :
					(inetAddress.getAddress().length == 4) ? 32 : 128;
		}

		boolean contains(InetAddress address) {
			if (!address.getClass().equals(inetAddress.getClass())) return false;

			byte[] targetIP = address.getAddress();
			byte[] cidrIP = inetAddress.getAddress();

			int maskBytes = mask / 8;
			int remainingBits = mask % 8;

			// 检查完整字节部分
			for (int i = 0; i < maskBytes; i++) {
				if (targetIP[i] != cidrIP[i]) return false;
			}

			// 处理剩余位
			if (remainingBits > 0) {
				byte maskByte = (byte) (0xFF << (8 - remainingBits));
				return (targetIP[maskBytes] & maskByte) == (cidrIP[maskBytes] & maskByte);
			}

			return true;
		}
	}
}