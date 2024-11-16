package com.hula.common;

import com.hula.core.user.domain.vo.req.user.LoginReq;
import com.hula.utils.AssertUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@Slf4j
public class AssertTest {

    @Test
    public void testAssert() {
        LoginReq loginReq = new LoginReq();
        AssertUtil.fastFailValidate(loginReq);
    }

}
