package com.luohuo.flex.ai.controller.model.vo.apikey;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * AI API 密钥余额查询响应 VO
 *
 * @author 乾乾
 */
@Schema(description = "管理后台 - AI API 密钥余额查询 Response VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiApiKeyBalanceRespVO {

	@Schema(description = "API Key ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "23538")
	private Long id;

	@Schema(description = "平台", requiredMode = Schema.RequiredMode.REQUIRED, example = "Moonshot")
	private String platform;

	@Schema(description = "是否支持余额查询", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
	private Boolean supported;

	@Schema(description = "是否查询成功", example = "true")
	private Boolean success;

	@Schema(description = "错误信息", example = "API Key无效")
	private String errorMessage;

	@Schema(description = "余额信息列表")
	private List<BalanceInfo> balanceInfos;

	@Schema(description = "总余额（所有币种的总和，统一转换为人民币）", example = "100.50")
	private BigDecimal totalBalance;

	/**
	 * 余额信息
	 */
	@Schema(description = "余额信息")
	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class BalanceInfo {

		@Schema(description = "币种", example = "CNY")
		private String currency;

		@Schema(description = "总余额", example = "110.00")
		private BigDecimal totalBalance;

		@Schema(description = "使用总量（按平台返回的单位，可能为金额或额度）", example = "12.34")
		private BigDecimal usageTotal;

		@Schema(description = "赠送余额", example = "10.00")
		private BigDecimal grantedBalance;

		@Schema(description = "充值余额", example = "100.00")
		private BigDecimal toppedUpBalance;

		@Schema(description = "是否可用", example = "true")
		private Boolean available;
	}
}