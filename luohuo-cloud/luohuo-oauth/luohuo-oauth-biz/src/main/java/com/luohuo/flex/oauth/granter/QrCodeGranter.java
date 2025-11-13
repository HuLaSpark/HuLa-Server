package com.luohuo.flex.oauth.granter;

import cn.dev33.satoken.config.SaTokenConfig;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.ObjectUtil;
import com.luohuo.basic.base.R;
import com.luohuo.basic.cache.redis2.CacheResult;
import com.luohuo.basic.cache.repository.CachePlusOps;
import com.luohuo.basic.context.ContextUtil;
import com.luohuo.basic.exception.BizException;
import com.luohuo.basic.model.cache.CacheKey;
import com.luohuo.flex.base.entity.tenant.DefUser;
import com.luohuo.flex.base.service.system.DefClientService;
import com.luohuo.flex.base.service.tenant.DefUserService;
import com.luohuo.flex.base.service.user.BaseEmployeeService;
import com.luohuo.flex.base.service.user.BaseOrgService;
import com.luohuo.flex.common.properties.SystemProperties;
import com.luohuo.flex.im.api.ImUserApi;
import com.luohuo.flex.model.redis.annotation.RedissonLock;
import com.luohuo.flex.oauth.AesUtil;
import com.luohuo.flex.oauth.cache.QrCacheKeyBuilder;
import com.luohuo.flex.oauth.emuns.QrLoginState;
import com.luohuo.flex.oauth.vo.param.ConfirmReq;
import com.luohuo.flex.oauth.vo.param.LoginParamVO;
import com.luohuo.flex.oauth.vo.param.QueryStatusReq;
import com.luohuo.flex.oauth.vo.param.ScanReq;
import com.luohuo.flex.oauth.vo.result.LoginResultVO;
import com.luohuo.flex.oauth.vo.result.QrCodeResp;
import com.luohuo.flex.oauth.vo.result.QrCodeResult;
import com.luohuo.flex.oauth.vo.result.QrCodeStatus;
import com.luohuo.flex.oauth.vo.result.ScanResp;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * 二维码扫码登录
 *
 * @author 乾乾
 * @date 2025年08月08日10:23:53
 */
@Component
@Slf4j
public class QrCodeGranter extends AbstractTokenGranter {

	@Resource
	protected CachePlusOps cachePlusOps;

	public QrCodeGranter(SystemProperties systemProperties, DefClientService defClientService, DefUserService defUserService, BaseEmployeeService baseEmployeeService, BaseOrgService baseOrgService, SaTokenConfig saTokenConfig, ImUserApi imUserApi, com.luohuo.flex.oauth.biz.StpInterfaceBiz stpInterfaceBiz) {
		super(systemProperties, defClientService, defUserService, baseEmployeeService, baseOrgService, saTokenConfig, imUserApi, stpInterfaceBiz);
	}

	public QrCodeResp generateQRCode() {
		QrLoginState pending = QrLoginState.PENDING;
		String qrId = UUID.randomUUID().toString();
		CacheKey cacheKey = QrCacheKeyBuilder.builder(qrId);
		cacheKey.setExpire(Duration.ofMillis(pending.getMillis()));
		// 2. 生成设备指纹
		String ip = ContextUtil.getIP();
		String deviceHash = makeRawDeviceHash(ip);

		// 存储初始状态（含设备指纹防劫持）
		QrCodeStatus state = new QrCodeStatus(pending.getValue(), deviceHash);
		cachePlusOps.set(cacheKey, state, true);
		return new QrCodeResp(qrId, deviceHash, ip, pending.getExpireTime());
    }

	/**
	 * 动态生成加密指纹 => IP+时间盐
	 * @return
	 */
	private static String makeRawDeviceHash(String ip) {
		String rawDeviceHash = ip + "|" + System.currentTimeMillis();
		return AesUtil.encrypt(rawDeviceHash);
	}

	/**
	 * 用户扫码
	 * @param req
	 * return 返回操作的过期时间
	 */
	@RedissonLock(prefixKey ="luohuo:handleScan:", key = "#req.qrId")
	public ScanResp handleScan(ScanReq req) {
		CacheKey cacheKey = QrCacheKeyBuilder.builder(req.getQrId());

		// 1. 校验二维码状态
		CacheResult<QrCodeStatus> cacheResult = cachePlusOps.get(cacheKey);
		QrCodeStatus qrCodeStatus = cacheResult.getValue();
		if (ObjectUtil.isNull(qrCodeStatus) || !QrLoginState.PENDING.getValue().equals(qrCodeStatus.getStatus())) {
			throw new BizException("二维码已失效");
		}

		// 2. 更新为已扫描状态
		String ip = ContextUtil.getIP();
//		if (!DeviceFingerprintValidator.validateDeviceFingerprint(makeRawDeviceHash(ip), qrCodeStatus.getDeviceHash())) {
//			throw new BizException("可疑设备");
//		}

		if (QrLoginState.SCANNED.getValue().equals(qrCodeStatus.getStatus())) {
			throw new BizException("二维码已扫描");
		}

		// 3. 更新redis 状态
		QrLoginState scanned = QrLoginState.SCANNED;
		cacheKey.setExpire(Duration.ofMillis(scanned.getMillis()));
		qrCodeStatus.setStatus(scanned.getValue());
		cachePlusOps.set(cacheKey, qrCodeStatus);
		return new ScanResp(ip, scanned.getExpireTime(), "PC");
	}

	/**
	 * 用户确认
	 * @param req
	 * return 返回操作的过期时间
	 */
	@RedissonLock(prefixKey ="luohuo:confirmLogin:", key = "#req.qrId")
	public Long confirmLogin(ConfirmReq req) {
		CacheKey statusKey = QrCacheKeyBuilder.builder(req.getQrId());

		// 状态校验（仅允许SCANNED→CONFIRMED）
		CacheResult<QrCodeStatus> cacheResult = cachePlusOps.get(statusKey);
		QrCodeStatus qrLoginState = cacheResult.getValue();
		if (ObjectUtil.isNull(qrLoginState) || !QrLoginState.SCANNED.getValue().equals(qrLoginState.getStatus())) {
			throw new BizException("二维码状态异常");
		}

		// 存储用户ID并更新状态
		QrLoginState confirmed = QrLoginState.CONFIRMED;
		statusKey.setExpire(Duration.ofMillis(confirmed.getMillis()));
		cachePlusOps.set(statusKey, new QrCodeStatus(confirmed.getValue(), qrLoginState.getDeviceHash(), ContextUtil.getUserId(), ContextUtil.getUid()));
		return confirmed.getExpireTime();
	}

	@RedissonLock(prefixKey ="luohuo:checkStatus:", key = "#req.deviceHash")
	public R checkStatus(QueryStatusReq req) {
		CacheKey statusKey = QrCacheKeyBuilder.builder(req.getQrId());
		CacheResult<QrCodeStatus> result = cachePlusOps.get(statusKey);
		QrCodeStatus qrCodeStatus = result.getValue();

		// 1. 校验数据
		if (qrCodeStatus == null) {
			return R.success(new QrCodeResult(QrLoginState.EXPIRED.getValue()));
		}
		if (!req.getDeviceHash().equals(qrCodeStatus.getDeviceHash())) {
			throw new BizException("设备状态异常");
		}

		String status = qrCodeStatus.getStatus();
		if (QrLoginState.CONFIRMED.getValue().equals(status)) {
			// 2. 查询用户信息
			Long userId = qrCodeStatus.getUserId();
			Long uid = qrCodeStatus.getUid();
			DefUser defUser = defUserService.getByIdCache(userId);

			// 3. 生成token
			return R.success(new QrCodeResult(QrLoginState.CONFIRMED.getValue(), buildResult(uid, defUser, findOrg(defUser), req.getDeviceType(), req.getClientId())));
		} else if (QrLoginState.SCANNED.getValue().equals(status)) {
			return R.success(new QrCodeResult(QrLoginState.SCANNED.getValue()));
		} else {
			return R.success(new QrCodeResult(QrLoginState.PENDING.getValue()));
		}
	}

	@Override
	protected R<LoginResultVO> checkParam(LoginParamVO loginParam) {
		return null;
	}

	@Override
	protected DefUser getUser(LoginParamVO loginParam) {
		return null;
	}
}
