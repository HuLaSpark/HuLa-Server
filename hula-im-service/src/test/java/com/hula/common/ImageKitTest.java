package com.hula.common;

import com.hula.HuLaImServiceApplication;
import io.imagekit.sdk.ImageKit;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * ImageKit测试类
 * @author nyh
 */
@SpringBootTest(classes = HuLaImServiceApplication.class)
@RunWith(SpringRunner.class)
@Slf4j
public class ImageKitTest {

    @Resource
    private ImageKit imageKit;

    @Test
    public void testImageKitAuth() {
        // 获取认证参数
        Map<String, String> authParams = imageKit.getAuthenticationParameters();
        
        // 验证认证参数不为空
        assertNotNull("认证参数不应为空", authParams);
        
        // 验证必要的认证参数是否存在
        assertTrue("token参数应存在", authParams.containsKey("token"));
        assertTrue("expire参数应存在", authParams.containsKey("expire"));
        assertTrue("signature参数应存在", authParams.containsKey("signature"));
        
        // 打印认证参数用于调试
        log.info("ImageKit认证参数: {}", authParams);
    }
} 