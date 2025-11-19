package com.baidu.fsg.uid;

public class Base62Encoder {
    private static final String BASE62_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = BASE62_CHARS.length();

	/**
	 * 按照uuid最大的值进行base62编码
	 * @param id
	 * @return
	 */
	public static String createAccount(long id){
		return "ID_" + encode(id, 19);
	}

	public static String createGroup(long id){
		return "hula_" + encode(id, 19);
	}

    /**
     * 将Long型ID编码为定长Base62字符串（可配置12-19位）
     */
    public static String encode(long id, int fixedLength) {
        StringBuilder sb = new StringBuilder();
        while (id > 0 && sb.length() < fixedLength) {
            sb.append(BASE62_CHARS.charAt((int)(id % BASE)));
            id /= BASE;
        }
        return sb.reverse().toString();
    }
}