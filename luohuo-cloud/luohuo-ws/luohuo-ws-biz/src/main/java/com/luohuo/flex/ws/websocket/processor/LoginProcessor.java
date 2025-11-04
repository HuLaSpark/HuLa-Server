//package com.luohuo.flex.ws.websocket.processor;
//
//import cn.hutool.json.JSONUtil;
//import com.luohuo.flex.model.entity.WSRespTypeEnum;
//import com.luohuo.flex.model.entity.WsBaseResp;
//import com.luohuo.flex.model.ws.WSBaseReq;
//import com.luohuo.flex.model.enums.WSReqTypeEnum;
//import com.luohuo.flex.ws.websocket.SessionManager;
//import jakarta.annotation.Resource;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.core.annotation.Order;
//import org.springframework.stereotype.Component;
//import org.springframework.web.reactive.socket.CloseStatus;
//import org.springframework.web.reactive.socket.WebSocketSession;
//import reactor.core.publisher.Mono;
//
///**
// * 扫码登录的逻辑
// */
//@Slf4j
//@Order(2)
//@Component
//public class LoginProcessor implements MessageProcessor {
//	@Resource
//	private SessionManager sessionManager;
//    @Override
//    public boolean supports(WSBaseReq req) {
//        return WSReqTypeEnum.LOGIN.equals(WSReqTypeEnum.of(req.getType()));
//    }
//
//    @Override
//    public void process(WebSocketSession session, Long uid, WSBaseReq req) {
//		try {
//			String qrToken = req.getData().getStr("qrToken");
//
//			// 1. 验证Token有效性
//			if (!qrCodeService.validateToken(qrToken)) {
//				session.send(Mono.just(session.textMessage(
//						JSONUtil.toJsonStr(WSBaseResp.fail("二维码已过期"))
//				))).subscribe();
//				return;
//			}
//
//			// 2. 绑定用户与会话（需手机端已确认）
//			if (qrCodeService.isConfirmed(qrToken)) {
//				sessionManager.bindUserToSession(qrToken, uid, session);
//				session.send(Mono.just(session.textMessage(
//						JSONUtil.toJsonStr(WsBaseResp.success("登录成功"))
//				))).subscribe();
//			} else {
//				session.send(Mono.just(session.textMessage(
//						JSONUtil.toJsonStr(WsBaseResp.of(WSRespTypeEnum.WAIT_CONFIRM))
//				))).subscribe();
//			}
//		} catch (Exception e) {
//			log.error("扫码登录处理失败", e);
//			session.close(CloseStatus.SERVER_ERROR).subscribe();
//		}
//	}
//    }
//}