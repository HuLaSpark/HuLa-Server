package com.luohuo.flex.oauth.service.impl;

import cn.hutool.core.thread.NamedThreadFactory;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.luohuo.basic.base.R;
import com.luohuo.basic.utils.JsonUtils;
import com.luohuo.basic.utils.TimeUtils;
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

import java.time.LocalDateTime;
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
            IpDetail ipDetail = tryGetIpDetailOrNullTreeTimes(ip);
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

    private static IpDetail tryGetIpDetailOrNullTreeTimes(String ip) {
        for (int i = 0; i < 3; i++) {
            IpDetail ipDetail = getIpDetailOrNull(ip);
            if (Objects.nonNull(ipDetail)) {
                return ipDetail;
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                log.error("get ip detail fail ip:{},uid:{}", ip, i);
            }
        }
        return null;
    }

    public static IpDetail getIpDetailOrNull(String ip) {
        String body = HttpUtil.get("https://ip.taobao.com/outGetIpInfo?ip=" + ip + "&accessKey=alibaba-inc");
        try {
            R<IpDetail> result = JsonUtils.toObj(body.replace("XX", "").replace("xx", ""), new TypeReference<>() {});
            return result.getData();
        } catch (Exception ignored) {
        }
        return null;
    }

    //测试耗时结果 100次查询总耗时约100s，平均一次成功查询需要1s,可以接受
    //第99次成功,目前耗时：99545ms
    public static void main(String[] args) {
		LocalDateTime begin = LocalDateTime.now();
        for (int i = 0; i < 100; i++) {
            int finalI = i;
            EXECUTOR.execute(() -> {
                IpDetail ipDetail = tryGetIpDetailOrNullTreeTimes("127.0.0.1");
                if (Objects.nonNull(ipDetail)) {
					LocalDateTime date = LocalDateTime.now();
                    System.out.println(String.format("第%d次成功,目前耗时：%dms", finalI, (TimeUtils.getTime(date) - TimeUtils.getTime(begin))));
                }
            });
        }
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
