package com.luohuo.flex.common.utils;

/**
 * 常用工具集合
 */
public class ToolsUtil {

	/**
	 * 根据长度生成随机数字
	 *
	 * @param start 起始数字
	 * @param end   结束数字
	 * @return 生成的随机码
	 */
	public static Integer randomCount(Integer start, Integer end) {
		return (int) (Math.random() * (end - start + 1) + start);
	}

	/**
	 * 将两个字符串组合成 {}_{} 格式
	 *
	 * @param part1 第一部分字符串
	 * @param part2 第二部分字符串
	 * @return 组合后的字符串，如果任一输入为null则返回null
	 */
	public static String combineStrings(String part1, String part2) {
		if (part1 == null || part2 == null) {
			return null;
		}
		return part1 + "_" + part2;
	}

	/**
	 * 将 {}_{} 格式的字符串解析为原始的两个字符串
	 *
	 * @param combinedString 组合后的字符串
	 * @return 包含两个元素的字符串数组，第一个元素是第一部分，第二个元素是第二部分。
	 *         如果输入为null或格式不正确，返回null。
	 */
	public static String[] parseCombinedString(String combinedString) {
		if (combinedString == null) {
			return null;
		}

		int separatorIndex = combinedString.indexOf('_');
		if (separatorIndex == -1 || separatorIndex == combinedString.length() - 1) {
			return null;
		}

		String part1 = combinedString.substring(0, separatorIndex);
		String part2 = combinedString.substring(separatorIndex + 1);

		return new String[]{part1, part2};
	}
}
