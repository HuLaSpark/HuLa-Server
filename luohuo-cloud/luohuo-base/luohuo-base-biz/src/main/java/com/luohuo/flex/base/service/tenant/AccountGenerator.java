package com.luohuo.flex.base.service.tenant;

import cn.hutool.crypto.digest.DigestUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 账号生成器
 * 生成11位纯数字账号
 *
 * @author 乾乾
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AccountGenerator {

	/**
	 * 基于用户ID生成唯一的11位纯数字账号
	 * 格式：1XXXXXXXXXX（第一位固定为1，后面10位由用户ID哈希生成）
	 *
	 * @param userId 用户ID（def_user.id）
	 * @return 11位纯数字账号
	 */
	public String generateAccountByUserId(Long userId) {
		if (userId == null || userId <= 0) {
			throw new IllegalArgumentException("用户ID不能为空或小于等于0");
		}

		// 使用 MD5 哈希算法
		String md5Hex = DigestUtil.md5Hex(String.valueOf(userId));

		// 取前10位十六进制转换为十进制
		String hex10 = md5Hex.substring(0, 10);
		long hashValue = Long.parseLong(hex10, 16);

		// 确保是11位数字，第一位是1
		long account = 10000000000L + (hashValue % 10000000000L);

		String accountStr = String.valueOf(account);
		log.debug("生成账号: {} (基于用户ID: {})", accountStr, userId);

		return accountStr;
	}
}