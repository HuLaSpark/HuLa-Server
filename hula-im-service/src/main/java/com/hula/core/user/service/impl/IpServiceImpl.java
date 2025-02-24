package com.hula.core.user.service.impl;

import cn.hutool.core.thread.NamedThreadFactory;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.hula.core.user.dao.UserDao;
import com.hula.core.user.domain.entity.IpDetail;
import com.hula.core.user.domain.entity.IpInfo;
import com.hula.core.user.domain.entity.User;
import com.hula.core.user.service.IpService;
import com.hula.domain.vo.res.ApiResult;
import com.hula.utils.JsonUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * ip
 * @author nyh
 */
@Service
@Slf4j
public class IpServiceImpl implements IpService, DisposableBean {
    private static final ExecutorService EXECUTOR = new ThreadPoolExecutor(1, 1,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(500),
            new NamedThreadFactory("refresh-ipDetail",false));

    @Resource
    private UserDao userDao;


    @Override
    public void refreshIpDetailAsync(User user) {
        EXECUTOR.execute(() -> {
            IpInfo ipInfo = user.getIpInfo();
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
                User update = new User();
                update.setId(user.getId());
                update.setIpInfo(ipInfo);
				user.setIpInfo(ipInfo);
                userDao.updateById(update);
            } else {
                log.error("get ip detail fail ip:{},uid:{}", ip, user.getId());
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
            ApiResult<IpDetail> result = JsonUtils.toObj(body, new TypeReference<ApiResult<IpDetail>>() {});
            return result.getData();
        } catch (Exception ignored) {
        }
        return null;
    }

    //测试耗时结果 100次查询总耗时约100s，平均一次成功查询需要1s,可以接受
    //第99次成功,目前耗时：99545ms
    public static void main(String[] args) {
        Date begin = new Date();
        for (int i = 0; i < 100; i++) {
            int finalI = i;
            EXECUTOR.execute(() -> {
                IpDetail ipDetail = tryGetIpDetailOrNullTreeTimes("113.90.36.126");
                if (Objects.nonNull(ipDetail)) {
                    Date date = new Date();
                    System.out.println(String.format("第%d次成功,目前耗时：%dms", finalI, (date.getTime() - begin.getTime())));
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
