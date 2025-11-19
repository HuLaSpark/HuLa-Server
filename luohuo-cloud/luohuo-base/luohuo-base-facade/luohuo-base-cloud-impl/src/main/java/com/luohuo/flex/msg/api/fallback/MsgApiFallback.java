package com.luohuo.flex.msg.api.fallback;

import org.springframework.stereotype.Component;
import com.luohuo.basic.base.R;
import com.luohuo.flex.msg.api.MsgApi;
import com.luohuo.flex.msg.vo.update.ExtendMsgSendVO;

/**
 * 熔断
 *
 * @author 乾乾
 * @date 2019/07/25
 */
@Component
public class MsgApiFallback implements MsgApi {
    @Override
    public R<Boolean> sendByTemplate(ExtendMsgSendVO data) {
        return R.timeout();
    }
}
