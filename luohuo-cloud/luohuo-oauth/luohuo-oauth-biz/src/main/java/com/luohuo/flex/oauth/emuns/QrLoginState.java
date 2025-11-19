package com.luohuo.flex.oauth.emuns;

public enum QrLoginState {
    /**
     * 待扫描（默认状态）
     * - 有效期：30秒（半分钟）
     * - 描述：二维码生成后等待用户扫描
     */
    PENDING("待扫描", "PENDING", 30_000),

	/**
	 * 已过期
	 * - 有效期：30秒
	 * - 描述：超时未扫描
	 */
	EXPIRED("已过期", "EXPIRED", 30_000),
    
    /**
     * 已扫描待确认
     * - 有效期：60秒（1分钟）
     * - 描述：用户已扫码但未确认登录
     */
    SCANNED("已扫描待确认", "SCANNED", 60_000),
    
    /**
     * 已确认（可换取Token）
     * - 有效期：10秒
     * - 描述：用户确认登录，等待PC端换取Token
     */
    CONFIRMED("已确认", "CONFIRMED", 10_000);

    private final String description; // 状态描述
	private final String value; // 状态值
    private final int millis; // 状态有效期（毫秒）

    // 枚举构造函数
    QrLoginState(String description, String value, int millis) {
        this.description = description;
		this.value = value;
        this.millis = millis;
    }

    // 获取状态描述
    public String getDescription() {
        return description;
    }

	public String getValue() {
		return value;
	}

	// 获取状态超时时间
    public int getMillis() {
        return millis;
    }

    // 获取状态过期时间点
    public Long getExpireTime() {
        return System.currentTimeMillis() + millis;
    }
}