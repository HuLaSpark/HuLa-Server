package com.luohuo.flex.ai.service.model;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.luohuo.flex.ai.common.pojo.PageResult;
import com.luohuo.flex.ai.controller.model.vo.apikey.AiApiKeyBalanceRespVO;
import com.luohuo.flex.ai.controller.model.vo.apikey.AiApiKeyPageReqVO;
import com.luohuo.flex.ai.controller.model.vo.apikey.AiApiKeySaveReqVO;
import com.luohuo.flex.ai.dal.model.AiApiKeyDO;
import com.luohuo.flex.ai.enums.AiPlatformEnum;
import com.luohuo.flex.ai.enums.CommonStatusEnum;
import com.luohuo.flex.ai.mapper.LambdaQueryWrapperX;
import com.luohuo.flex.ai.mapper.model.AiApiKeyMapper;
import com.luohuo.flex.ai.utils.BeanUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.luohuo.flex.ai.enums.ErrorCodeConstants.API_KEY_DISABLE;
import static com.luohuo.flex.ai.enums.ErrorCodeConstants.API_KEY_NOT_EXISTS;
import static com.luohuo.flex.ai.utils.ServiceExceptionUtil.exception;

/**
 * AI API 密钥 Service 实现类
 *
 * @author 乾乾
 */
@Slf4j
@Service
@Validated
public class AiApiKeyServiceImpl implements AiApiKeyService {

	@Resource
	private AiApiKeyMapper apiKeyMapper;

	@Override
	public Long createApiKey(AiApiKeySaveReqVO createReqVO, Long userId) {
		// 插入
		AiApiKeyDO apiKey = BeanUtils.toBean(createReqVO, AiApiKeyDO.class);

		// 如果是私有密钥，设置 userId
		if (createReqVO.getPublicStatus() == null || Boolean.FALSE.equals(createReqVO.getPublicStatus())) {
			apiKey.setUserId(userId)
					.setPublicStatus(false);
			// 私有密钥默认启用
			if (createReqVO.getStatus() == null) {
				apiKey.setStatus(CommonStatusEnum.ENABLE.getStatus());
			}
		}

		apiKeyMapper.insert(apiKey);
		return apiKey.getId();
	}

	@Override
	public void updateApiKey(AiApiKeySaveReqVO updateReqVO, Long userId) {
		// 校验存在
		AiApiKeyDO apiKey = validateApiKeyExists(updateReqVO.getId());

		// 权限校验
		if (Boolean.FALSE.equals(apiKey.getPublicStatus())) {
			// 私有密钥，只有创建者可以编辑
			if (ObjectUtil.notEqual(apiKey.getUserId(), userId)) {
				throw exception(API_KEY_NOT_EXISTS);
			}
		} else {
			// 公开密钥，需要管理员权限
			if (userId >= 10937855681025L) {
				throw exception(API_KEY_NOT_EXISTS);
			}
		}

		// 更新
		AiApiKeyDO updateObj = BeanUtils.toBean(updateReqVO, AiApiKeyDO.class);
		apiKeyMapper.updateById(updateObj);
	}

	@Override
	public void deleteApiKey(Long id, Long userId) {
		// 校验存在
		AiApiKeyDO apiKey = validateApiKeyExists(id);

		// 权限校验
		if (Boolean.FALSE.equals(apiKey.getPublicStatus())) {
			// 私有密钥，只有创建者可以删除
			if (ObjectUtil.notEqual(apiKey.getUserId(), userId)) {
				throw exception(API_KEY_NOT_EXISTS);
			}
		} else {
			// 公开密钥，需要管理员权限
			if (userId >= 10937855681025L) {
				throw exception(API_KEY_NOT_EXISTS);
			}
		}

		// 删除
		apiKeyMapper.deleteById(id);
	}

	@Override
	public void updateApiKeyAdmin(AiApiKeySaveReqVO updateReqVO) {
		validateApiKeyExists(updateReqVO.getId());

		// 管理员更新，无权限校验
		AiApiKeyDO updateObj = BeanUtils.toBean(updateReqVO, AiApiKeyDO.class);
		apiKeyMapper.updateById(updateObj);
	}

	@Override
	public void deleteApiKeyAdmin(Long id) {
		// 校验存在
		validateApiKeyExists(id);

		// 管理员删除，无权限校验
		apiKeyMapper.deleteById(id);
	}

	private AiApiKeyDO validateApiKeyExists(Long id) {
		AiApiKeyDO apiKey = apiKeyMapper.selectById(id);
		if (apiKey == null) {
			throw exception(API_KEY_NOT_EXISTS);
		}
		return apiKey;
	}

	@Override
	public AiApiKeyDO getApiKey(Long id) {
		return apiKeyMapper.selectById(id);
	}

	@Override
	public AiApiKeyDO validateApiKey(Long id) {
		AiApiKeyDO apiKey = validateApiKeyExists(id);
		if (CommonStatusEnum.isDisable(apiKey.getStatus())) {
			throw exception(API_KEY_DISABLE);
		}
		return apiKey;
	}

	@Override
	public PageResult<AiApiKeyDO> getApiKeyPage(AiApiKeyPageReqVO pageReqVO, Long userId) {
		return apiKeyMapper.selectPageByMy(pageReqVO, userId);
	}

	@Override
	public List<AiApiKeyDO> getApiKeyList(Long userId) {
		// 返回所有公开密钥 + 用户私有密钥
		return apiKeyMapper.selectList(new LambdaQueryWrapperX<AiApiKeyDO>()
				.and(w -> w
						.eq(AiApiKeyDO::getPublicStatus, true)
						.or()
						.eq(AiApiKeyDO::getUserId, userId)
				)
				.orderByDesc(AiApiKeyDO::getId));
	}

	@Override
	public List<AiApiKeyDO> getAllApiKeyList() {
		// 返回所有 API 密钥（后台管理专用）
		return apiKeyMapper.selectList(new LambdaQueryWrapperX<AiApiKeyDO>()
				.orderByDesc(AiApiKeyDO::getId));
	}

	@Override
	public PageResult<AiApiKeyDO> getAdminApiKeyPage(AiApiKeyPageReqVO pageReqVO) {
		return apiKeyMapper.selectPage(pageReqVO);
	}

	@Override
	public AiApiKeyDO getRequiredDefaultApiKey(String platform, Integer status) {
		AiApiKeyDO apiKey = apiKeyMapper.selectFirstByPlatformAndStatus(platform, status);
		if (apiKey == null) {
			throw exception(API_KEY_NOT_EXISTS);
		}
		return apiKey;
	}

	@Override
	public AiApiKeyBalanceRespVO getApiKeyBalance(Long id, Long userId) {
		// 1. 校验 API Key 是否存在
		AiApiKeyDO apiKey = validateApiKeyExists(id);

		// 2. 权限校验
		if (Boolean.FALSE.equals(apiKey.getPublicStatus())) {
			// 私有密钥，只有创建者可以查询
			if (ObjectUtil.notEqual(apiKey.getUserId(), userId)) {
				throw exception(API_KEY_NOT_EXISTS);
			}
		}

		// 3. 根据平台查询余额
		AiPlatformEnum platformEnum;
		try {
			platformEnum = AiPlatformEnum.validatePlatform(apiKey.getPlatform());
		} catch (IllegalArgumentException e) {
			return AiApiKeyBalanceRespVO.builder()
					.id(id)
					.platform(apiKey.getPlatform())
					.supported(false)
					.success(false)
					.errorMessage("不支持的平台: " + apiKey.getPlatform())
					.build();
		}

		return queryBalanceByPlatform(apiKey, platformEnum);
	}

	/**
	 * 根据平台查询余额
	 */
	private AiApiKeyBalanceRespVO queryBalanceByPlatform(AiApiKeyDO apiKey, AiPlatformEnum platform) {
		try {
			switch (platform) {
				case MOONSHOT:
					return queryMoonshotBalance(apiKey);
				case DEEP_SEEK:
					return queryDeepSeekBalance(apiKey);
				case SILICON_FLOW:
					return querySiliconFlowBalance(apiKey);
				case OPENROUTER:
					return queryOpenRouterBalance(apiKey);
				default:
					return AiApiKeyBalanceRespVO.builder()
							.id(apiKey.getId())
							.platform(apiKey.getPlatform())
							.supported(false)
							.success(false)
							.errorMessage("该平台暂不支持余额查询")
							.build();
			}
		} catch (Exception e) {
			log.error("查询API Key余额失败, platform={}, error={}", platform.getPlatform(), e.getMessage(), e);
			return AiApiKeyBalanceRespVO.builder()
					.id(apiKey.getId())
					.platform(apiKey.getPlatform())
					.supported(true)
					.success(false)
					.errorMessage("查询失败: " + e.getMessage())
					.build();
		}
	}

	/**
	 * 查询月之暗面(Moonshot/Kimi)余额
	 * API文档: https://platform.moonshot.cn/docs/api/balance
	 */
	private AiApiKeyBalanceRespVO queryMoonshotBalance(AiApiKeyDO apiKey) {
		String baseUrl = StrUtil.isNotBlank(apiKey.getUrl()) ? apiKey.getUrl() : "https://api.moonshot.cn";
		String url = baseUrl + "/v1/users/me/balance";

		HttpResponse response = HttpRequest.get(url)
				.header("Authorization", "Bearer " + apiKey.getApiKey())
				.header("Content-Type", "application/json")
				.timeout(10000)
				.execute();

		if (!response.isOk()) {
			throw new RuntimeException("HTTP请求失败: " + response.getStatus());
		}

		JSONObject jsonResponse = JSONUtil.parseObj(response.body());

		// 解析响应
		List<AiApiKeyBalanceRespVO.BalanceInfo> balanceInfos = new ArrayList<>();
		BigDecimal totalBalance = BigDecimal.ZERO;

		if (jsonResponse.containsKey("data")) {
			JSONObject data = jsonResponse.getJSONObject("data");
			if (data.containsKey("available_balance")) {
				BigDecimal balance = data.getBigDecimal("available_balance");
				balanceInfos.add(AiApiKeyBalanceRespVO.BalanceInfo.builder()
						.currency("CNY")
						.totalBalance(balance)
						.available(true)
						.build());
				totalBalance = balance;
			}
		}

		return AiApiKeyBalanceRespVO.builder()
				.id(apiKey.getId())
				.platform(apiKey.getPlatform())
				.supported(true)
				.success(true)
				.balanceInfos(balanceInfos)
				.totalBalance(totalBalance)
				.build();
	}

	/**
	 * 查询DeepSeek余额
	 * API文档: https://api-docs.deepseek.com/api/get-user-balance
	 */
	private AiApiKeyBalanceRespVO queryDeepSeekBalance(AiApiKeyDO apiKey) {
		String baseUrl = StrUtil.isNotBlank(apiKey.getUrl()) ? apiKey.getUrl() : "https://api.deepseek.com";
		String url = baseUrl + "/user/balance";

		HttpResponse response = HttpRequest.get(url)
				.header("Authorization", "Bearer " + apiKey.getApiKey())
				.header("Accept", "application/json")
				.timeout(10000)
				.execute();

		if (!response.isOk()) {
			throw new RuntimeException("HTTP请求失败: " + response.getStatus());
		}

		JSONObject jsonResponse = JSONUtil.parseObj(response.body());

		// 解析响应
		List<AiApiKeyBalanceRespVO.BalanceInfo> balanceInfos = new ArrayList<>();
		BigDecimal totalBalance = BigDecimal.ZERO;

		if (jsonResponse.getBool("is_available", false) && jsonResponse.containsKey("balance_infos")) {
			for (Object item : jsonResponse.getJSONArray("balance_infos")) {
				JSONObject balanceInfo = (JSONObject) item;
				String currency = balanceInfo.getStr("currency");
				BigDecimal total = new BigDecimal(balanceInfo.getStr("total_balance"));
				BigDecimal granted = balanceInfo.containsKey("granted_balance")
						? new BigDecimal(balanceInfo.getStr("granted_balance")) : BigDecimal.ZERO;
				BigDecimal toppedUp = balanceInfo.containsKey("topped_up_balance")
						? new BigDecimal(balanceInfo.getStr("topped_up_balance")) : BigDecimal.ZERO;

				balanceInfos.add(AiApiKeyBalanceRespVO.BalanceInfo.builder()
						.currency(currency)
						.totalBalance(total)
						.grantedBalance(granted)
						.toppedUpBalance(toppedUp)
						.available(true)
						.build());

				// 简单累加（实际应该考虑汇率转换）
				totalBalance = totalBalance.add(total);
			}
		}

		return AiApiKeyBalanceRespVO.builder()
				.id(apiKey.getId())
				.platform(apiKey.getPlatform())
				.supported(true)
				.success(true)
				.balanceInfos(balanceInfos)
				.totalBalance(totalBalance)
				.build();
	}

	/**
	 * 查询硅基流动余额
	 * API文档: https://docs.siliconflow.cn/cn/api-reference/userinfo/get-user-info
	 */
	private AiApiKeyBalanceRespVO querySiliconFlowBalance(AiApiKeyDO apiKey) {
		String baseUrl = StrUtil.isNotBlank(apiKey.getUrl()) ? apiKey.getUrl() : "https://api.siliconflow.cn";
		String url = baseUrl + "/v1/user/info";

		HttpResponse response = HttpRequest.get(url)
				.header("Authorization", "Bearer " + apiKey.getApiKey())
				.header("Content-Type", "application/json")
				.timeout(10000)
				.execute();

		if (!response.isOk()) {
			throw new RuntimeException("HTTP请求失败: " + response.getStatus());
		}

		JSONObject jsonResponse = JSONUtil.parseObj(response.body());

		// 解析响应
		List<AiApiKeyBalanceRespVO.BalanceInfo> balanceInfos = new ArrayList<>();
		BigDecimal totalBalance = BigDecimal.ZERO;

		if (jsonResponse.getInt("code") == 20000 && jsonResponse.containsKey("data")) {
			JSONObject data = jsonResponse.getJSONObject("data");

			// 硅基流动返回的余额字段
			BigDecimal balance = data.getBigDecimal("balance", BigDecimal.ZERO);
			BigDecimal chargeBalance = data.getBigDecimal("chargeBalance", BigDecimal.ZERO);
			BigDecimal total = data.getBigDecimal("totalBalance", BigDecimal.ZERO);

			balanceInfos.add(AiApiKeyBalanceRespVO.BalanceInfo.builder()
					.currency("CNY")
					.totalBalance(total)
					.grantedBalance(balance.subtract(chargeBalance).max(BigDecimal.ZERO))
					.toppedUpBalance(chargeBalance)
					.available("normal".equals(data.getStr("status")))
					.build());

			totalBalance = total;
		}

		return AiApiKeyBalanceRespVO.builder()
				.id(apiKey.getId())
				.platform(apiKey.getPlatform())
				.supported(true)
				.success(true)
				.balanceInfos(balanceInfos)
				.totalBalance(totalBalance)
				.build();
	}

	private AiApiKeyBalanceRespVO queryOpenRouterBalance(AiApiKeyDO apiKey) {
		String baseUrl = StrUtil.isNotBlank(apiKey.getUrl()) ? apiKey.getUrl() : "https://openrouter.ai/api";
		String url = baseUrl + "/v1/key";

		HttpResponse response = HttpRequest.get(url)
				.header("Authorization", "Bearer " + apiKey.getApiKey())
				.header("Accept", "application/json")
				.timeout(10000)
				.execute();

		if (!response.isOk()) {
			throw new RuntimeException("HTTP请求失败: " + response.getStatus());
		}

		JSONObject jsonResponse = JSONUtil.parseObj(response.body());
		JSONObject data = jsonResponse.getJSONObject("data");
		List<AiApiKeyBalanceRespVO.BalanceInfo> balanceInfos = new ArrayList<>();
		BigDecimal totalBalance = BigDecimal.ZERO;
		Boolean freeTier = data == null ? null : data.getBool("is_free_tier");
		BigDecimal limitRemaining = null;
		if (data != null && data.get("limit_remaining") != null && !(data.get("limit_remaining") instanceof cn.hutool.json.JSONNull)) {
			limitRemaining = new BigDecimal(String.valueOf(data.get("limit_remaining")));
		}
		if (limitRemaining != null) {
			balanceInfos.add(AiApiKeyBalanceRespVO.BalanceInfo.builder()
					.currency("USD")
					.totalBalance(limitRemaining)
					.available(true)
					.build());
			totalBalance = limitRemaining;
		} else {
			BigDecimal limit = null;
			BigDecimal usage = null;
			if (data != null && data.get("limit") != null && !(data.get("limit") instanceof cn.hutool.json.JSONNull)) {
				limit = new BigDecimal(String.valueOf(data.get("limit")));
			}
            if (data != null && data.get("usage") != null && !(data.get("usage") instanceof cn.hutool.json.JSONNull)) {
                usage = new BigDecimal(String.valueOf(data.get("usage")));
            }
			BigDecimal remaining = null;
			if (limit != null) {
				remaining = usage != null ? limit.subtract(usage) : limit;
			}
            if (remaining != null) {
                balanceInfos.add(AiApiKeyBalanceRespVO.BalanceInfo.builder()
                        .currency("USD")
                        .totalBalance(remaining)
                        .usageTotal(usage)
                        .available(true)
                        .build());
                totalBalance = remaining;
            } else {
                balanceInfos.add(AiApiKeyBalanceRespVO.BalanceInfo.builder()
                        .currency("USD")
                        .totalBalance(BigDecimal.ZERO)
                        .grantedBalance(BigDecimal.ZERO)
                        .toppedUpBalance(BigDecimal.ZERO)
                        .usageTotal(BigDecimal.ZERO)
                        .available(Boolean.TRUE.equals(freeTier))
                        .build());
                totalBalance = BigDecimal.ZERO;
            }
		}

		return AiApiKeyBalanceRespVO.builder()
				.id(apiKey.getId())
				.platform(apiKey.getPlatform())
				.supported(true)
				.success(true)
				.balanceInfos(balanceInfos)
				.totalBalance(totalBalance)
				.build();
	}

}