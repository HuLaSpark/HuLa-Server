package com.hula.common.utils;

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
}
