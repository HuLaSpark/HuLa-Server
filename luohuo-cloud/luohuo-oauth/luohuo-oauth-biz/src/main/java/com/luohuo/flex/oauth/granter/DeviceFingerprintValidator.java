package com.luohuo.flex.oauth.granter;

import com.luohuo.basic.context.ContextUtil;
import com.luohuo.flex.oauth.AesUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DeviceFingerprintValidator {

    /**
     * 验证设备指纹的完整性与一致性 [目前仅仅判断了ip是否一致]
     * @param currentFingerprint 当前设备指纹
     * @param storedFingerprint 服务端存储的原始指纹
     * @return 验证结果（true=合法设备，false=可疑设备）
     */
    public static boolean validateDeviceFingerprint(String currentFingerprint, String storedFingerprint) {
        // 1. 基础格式校验
		String decryptedHash = AesUtil.decrypt(storedFingerprint);
		if (decryptedHash == null) {
			log.warn("设备指纹解密失败");
			return false;
		}

		// 2. 解析动态盐值
		String[] parts = decryptedHash.split("\\|");
		if (parts.length < 2) return false;

		String storedFullIp = parts[0];
		long timestamp = Long.parseLong(parts[1]);

		return storedFullIp.equals(ContextUtil.getIP()) && (System.currentTimeMillis() - timestamp < 3600_000);
	}

}