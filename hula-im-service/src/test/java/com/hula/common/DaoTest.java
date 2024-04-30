package com.hula.common;

import com.hula.HuLaImCommonStarterApplication;
import com.hula.common.user.service.LoginService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@SpringBootTest(classes = HuLaImCommonStarterApplication.class)
@RunWith(SpringRunner.class)
@Slf4j
public class DaoTest {
    @Resource
    private RedissonClient redissonClient;
    @Resource
    private LoginService loginService;

    @Test
    public void redis() {
        RLock lock = redissonClient.getLock("123");
        lock.lock();
        System.out.println("触发");
        lock.unlock();
    }

    @Test
    public void getUserToken() {
        String s = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1aWQiOjExMDAwLCJjcmVhdGVUaW1lIjoxNzE0NDkwNjc5fQ.Gu9iLk37OlKMVHsDnzvu6C9-tzt_5WRo9A11DkH9QlY";
        Long validUid = loginService.getValidUid(s);
        System.out.println(validUid);
    }
}
