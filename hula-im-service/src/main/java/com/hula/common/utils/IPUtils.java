package com.hula.common.utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * IP 工具类
 *
 * @author 乾乾
 */
@Slf4j
public class IPUtils {

	// 需要检查的代理头，按优先级排序
	private static final String[] HEADERS_TO_CHECK = {
			"CF-Connecting-IP",         // Cloudflare
			"True-Client-IP",           // Akamai
			"X-Original-Forwarded-For", // Kubernetes
			"X-Client-IP",
			"X-Real-IP",
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
						IPWithMask cidrRange;
						try {
							cidrRange = new IPWithMask(cidr);
						} catch (UnknownHostException e) {
							throw new RuntimeException(e);
						}
						return cidrRange.contains(address);
					});
		} catch (UnknownHostException e) {
			return false;
		}
	}

	/**
	 * 解析代理头值
	 */
	private static String parseProxyHeader(String headerValue) {
		if (headerValue.contains(",")) {
			List<String> ips = Arrays.stream(headerValue.split(","))
					.map(String::trim)
					.map(IPUtils::extractPureIP)
					.filter(IPUtils::isValidIPFormat)
					.collect(Collectors.toList());

			// 优先选择首个公网IP
			for (String candidate : ips) {
				if (!isPrivateIP(candidate)) {
					return candidate;
				}
			}
			// 如果没有公网IP则返回第一个合法IP
			return ips.isEmpty() ? null : ips.get(0);
		}
		return extractPureIP(headerValue);
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
				!isValidIPFormat(ip) ||
				"127.0.0.1".equals(ip);
	}

	public static String getHostIp(HttpServletRequest request) {
		String ip = null;

		// 1. 检查代理头
		for (String header : HEADERS_TO_CHECK) {
			String headerValue = request.getHeader(header);
			if (isValidHeaderValue(headerValue)) {
				ip = parseProxyHeader(headerValue);
				if (ip != null) {
					log.debug("Found valid IP [{}] in header: {}", ip, header);
					break;
				}
			}
		}

		// 2. 回退到远端地址
		if (isInvalidIP(ip)) {
			ip = request.getRemoteAddr();
			log.debug("使用远程ip地址: {}", ip);
		}

		// 3. 处理特殊地址
		ip = normalizeSpecialIPs(ip);

		// 4. 最终验证
		return isValidPublicIP(ip) ? ip : null;
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