package com.luohuo.flex.oauth;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.util.Base64;

public class AesUtil {
    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final byte[] SECRET_KEY = "Lu0Huo@32ByteKey!!1234567890ABCD".getBytes();

    public static String encrypt(String plainText) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY, "AES");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);

            byte[] iv = cipher.getIV(); // GCM需要IV
            byte[] encrypted = cipher.doFinal(plainText.getBytes());

            // 组合IV+密文便于存储
            return Base64.getEncoder().encodeToString(
                ByteBuffer.allocate(iv.length + encrypted.length)
                    .put(iv)
                    .put(encrypted)
                    .array()
            );
        } catch (Exception e) {
            throw new RuntimeException("加密失败", e);
        }
    }

	public static String decrypt(String encryptedText) {
		try {
			byte[] decoded = Base64.getDecoder().decode(encryptedText);
			ByteBuffer buffer = ByteBuffer.wrap(decoded);

			// 提取IV
			byte[] iv = new byte[12];
			buffer.get(iv);

			// 提取密文
			byte[] cipherText = new byte[buffer.remaining()];
			buffer.get(cipherText);

			// 解密
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY, "AES");
			cipher.init(Cipher.DECRYPT_MODE, keySpec, new GCMParameterSpec(128, iv));

			return new String(cipher.doFinal(cipherText));
		} catch (Exception e) {
			return null;
		}
	}
}