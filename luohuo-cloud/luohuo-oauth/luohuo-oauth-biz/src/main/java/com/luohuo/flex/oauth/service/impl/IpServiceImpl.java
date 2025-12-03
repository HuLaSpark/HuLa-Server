package com.luohuo.flex.oauth.service.impl;

import cn.hutool.core.thread.NamedThreadFactory;
import cn.hutool.core.util.StrUtil;
import com.luohuo.flex.base.entity.tenant.DefUser;
import com.luohuo.flex.base.service.tenant.DefUserService;
import com.luohuo.flex.im.api.ImUserApi;
import com.luohuo.flex.model.entity.base.IpDetail;
import com.luohuo.flex.model.entity.base.IpInfo;
import com.luohuo.flex.model.entity.base.RefreshIpInfo;
import com.luohuo.flex.oauth.service.IpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Service;

import com.luohuo.basic.log.util.AddressUtil;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * ip物理地址获取
 * @author 乾乾
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class IpServiceImpl implements IpService, DisposableBean {
	private static final ExecutorService EXECUTOR = new ThreadPoolExecutor(1, 1,
			0L, TimeUnit.MILLISECONDS,
			new LinkedBlockingQueue<>(500),
			new NamedThreadFactory("refresh-ipDetail",false));

	private final ImUserApi userApi;
	private final DefUserService defUserService;

	@Override
	public void refreshIpDetailAsync(Long uid, Long userId, IpInfo ipInfo) {
		EXECUTOR.execute(() -> {
			if (Objects.isNull(ipInfo)) {
				return;
			}
			String ip = ipInfo.needRefreshIp();
			if (StrUtil.isBlank(ip)) {
				return;
			}
			IpDetail ipDetail = getIpDetailOrNull(ip);
			if (Objects.nonNull(ipDetail)) {
				ipInfo.refreshIpDetail(ipDetail);
				DefUser update = new DefUser();
				update.setId(userId);
				update.setIpInfo(ipInfo);
				defUserService.updateById(update);

				userApi.refreshIpInfo(new RefreshIpInfo(uid, ipInfo));
			} else {
				log.error("get ip detail fail ip:{},userId:{}", ip, userId);
			}
		});
	}

    public static IpDetail getIpDetailOrNull(String ip) {
        try {
            if (StrUtil.isBlank(ip)) {
                return null;
            }
            String region = AddressUtil.getRegion(ip);
            if (StrUtil.isBlank(region) || "localhost".equalsIgnoreCase(region)) {
                return null;
            }
            String[] arr = region.split("\\|");
            for (int i = 0; i < arr.length; i++) {
                String v = StrUtil.trim(arr[i]);
                arr[i] = "0".equals(v) ? StrUtil.EMPTY : v;
            }
            IpDetail detail = new IpDetail();
            detail.setIp(ip);
            int len = arr.length;
            if (len >= 1) detail.setCountry(arr[0]);
            if (len == 5) {
                detail.setArea(arr[1]);
                detail.setRegion(arr[2]);
                detail.setCity(normalizeCity(arr[3]));
                detail.setIsp(arr[4]);
            } else if (len == 4) {
                detail.setArea(StrUtil.EMPTY);
                detail.setRegion(arr[1]);
                detail.setCity(normalizeCity(arr[2]));
                detail.setIsp(arr[3]);
            } else if (len == 3) {
                detail.setRegion(arr[1]);
                detail.setCity(normalizeCity(arr[2]));
            } else if (len == 2) {
                detail.setRegion(arr[1]);
            }
            return detail;
        } catch (Exception e) {
            return null;
        }
    }

    private static String normalizeCity(String city) {
        if (StrUtil.isBlank(city)) {
            return city;
        }
        String v = StrUtil.trim(city);
        return StrUtil.removeSuffix(v, "市");
    }

	@Override
	public void destroy() throws InterruptedException {
		EXECUTOR.shutdown();
		if (!EXECUTOR.awaitTermination(30, TimeUnit.SECONDS)) {
			//最多等30秒，处理不完就拉倒
			if (log.isErrorEnabled()) {
				log.error("Timed out while waiting for executor [{}] to terminate", EXECUTOR);
			}
		}

	}

}