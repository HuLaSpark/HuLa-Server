package com.hula.core.user.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.hula.common.config.ThreadPoolConfig;
import com.hula.common.constant.RedisKey;
import com.hula.common.domain.dto.LoginMessageDTO;
import com.hula.common.domain.dto.ScanSuccessMessageDTO;
import com.hula.common.enums.LoginTypeEnum;
import com.hula.common.event.UserOnlineEvent;
import com.hula.core.user.dao.UserDao;
import com.hula.core.user.domain.entity.User;
import com.hula.core.user.domain.enums.RoleTypeEnum;
import com.hula.core.user.domain.enums.WsBaseResp;
import com.hula.core.user.domain.vo.req.ws.WSAuthorize;
import com.hula.core.user.domain.vo.resp.user.LoginResultVO;
import com.hula.core.user.domain.vo.resp.user.OffLineResp;
import com.hula.core.user.service.LoginService;
import com.hula.core.user.service.RoleService;
import com.hula.core.user.service.TokenService;
import com.hula.core.user.service.WebSocketService;
import com.hula.core.user.service.adapter.WsAdapter;
import com.hula.core.user.service.cache.UserCache;
import com.hula.core.websocket.NettyUtil;
import com.hula.service.MQProducer;
import com.hula.utils.JwtUtils;
import com.hula.utils.RedisUtils;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

/**
 * @author nyh
 */
@Service
@Slf4j
public class WXMsgServiceImpl implements WebSocketService {

    // 缓存过期时间
    private static final Duration EXPIRE_TIME = Duration.ofHours(1);
    private static final Long MAX_MUM_SIZE = 10000L;
    /**
     * 所有请求登录的code与channel关系
     */
    public static final Cache<Integer, Channel> WAIT_LOGIN_MAP = Caffeine.newBuilder()
            .expireAfterWrite(EXPIRE_TIME)
            .maximumSize(MAX_MUM_SIZE)
            .build();
    /**
     * 所有已连接的websocket连接列表和一些额外参数
     */
    private static final ConcurrentHashMap<String, Channel> ONLINE_WS_MAP = new ConcurrentHashMap<>();

    /**
     * 所有在线的用户和对应的socket
     */
    private static final ConcurrentHashMap<Long, CopyOnWriteArrayList<Channel>> ONLINE_UID_MAP = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<String, Channel> getOnlineMap() {
        return ONLINE_WS_MAP;
    }

    /**
     * redis保存loginCode的key
     */
    private static final String LOGIN_CODE = "loginCode";

    @Resource
    private WxMpService wxMpService;
    @Resource
    private LoginService loginService;
    @Resource
    private TokenService tokenService;
    @Resource
    private UserDao userDao;
    @Resource
    private ApplicationEventPublisher applicationEventPublisher;
    @Resource
    @Qualifier(ThreadPoolConfig.WS_EXECUTOR)
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;
    @Resource
    private UserCache userCache;
    @Resource
    private RoleService roleService;
    @Resource
    private MQProducer mqProducer;

    /**
     * 处理用户登录请求，需要返回一张带code的二维码
     *
     * @param channel 通道
     */
    @SneakyThrows
    @Override
    public void handleLogin(Channel channel) {
        // 生成随机不重复的登录码,并将channel存在本地cache中
        Integer code = generateLoginCode(channel);
        // 请求微信接口，获取登录码地址
        WxMpQrCodeTicket wxMpQrCodeTicket = wxMpService.getQrcodeService()
                .qrCodeCreateTmpTicket(code, (int) EXPIRE_TIME.getSeconds());
        // 返回给前端（channel必在本地）
        sendMsg(channel, WsAdapter.buildLoginResp(wxMpQrCodeTicket));
    }

    /**
     * 获取不重复的登录的code，微信要求最大不超过int的存储极限
     * 防止并发，可以给方法加上synchronize，也可以使用cas乐观锁
     *
     * @return code
     */
    private Integer generateLoginCode(Channel channel) {
        int inc;
        do {
            // 本地cache时间必须比redis key过期时间短，否则会出现并发问题
            inc = RedisUtils.integerInc(RedisKey.getKey(LOGIN_CODE), (int) EXPIRE_TIME.toMinutes(), TimeUnit.MINUTES);
        } while (WAIT_LOGIN_MAP.asMap().containsKey(inc));
        // 储存一份在本地
        WAIT_LOGIN_MAP.put(inc, channel);
        return inc;
    }

    /**
     * 处理所有ws连接的事件
     *
     * @param channel 通道
     */
    @Override
    public void connect(Channel channel) {
        // 保证每个客户端只有一个连接
        String clientId = NettyUtil.getAttr(channel, NettyUtil.CLIENT_ID);
        Channel oldChannel = ONLINE_WS_MAP.get(clientId);
        if(Objects.nonNull(oldChannel)) {
            oldChannel.close();
            ONLINE_WS_MAP.remove(clientId);
        }
        ONLINE_WS_MAP.put(clientId , channel);
    }

    @Override
    public void remove(Channel channel) {
        Optional<Long> uid = Optional.ofNullable(NettyUtil.getAttr(channel, NettyUtil.UID));
        // 移除连接
        ONLINE_WS_MAP.remove((String)NettyUtil.getAttr(channel, NettyUtil.CLIENT_ID));
        // 已登录用户更新
        uid.ifPresent(id -> {
            CopyOnWriteArrayList<Channel> channels = ONLINE_UID_MAP.get(uid.get());
            if (CollectionUtil.isNotEmpty(channels)) {
                channels.removeIf(ch -> Objects.equals(ch.id(), channel.id()));
            }
            // 发布下线通知
            // 下线通知改为应用关闭或者切换账号时
//            applicationEventPublisher
//                    .publishEvent(new UserOfflineEvent(this,
//                            User.builder().id(id).lastOptTime(new Date()).build()));
        });
    }

    @Override
    public void authorize(Channel channel, WSAuthorize wsAuthorize) {
        // 用户校验成功给用户登录
        if (tokenService.verify(wsAuthorize.getToken())) {
            User user = userDao.getById(JwtUtils.getUidOrNull(wsAuthorize.getToken()));
            loginSuccess(channel, user, wsAuthorize.getToken(), wsAuthorize.getToken());
        }
        // 让前端的token失效
        else {
            sendMsg(channel, WsAdapter.buildInvalidateTokenResp(new OffLineResp(JwtUtils.getUidOrNull(wsAuthorize.getToken()),  LoginTypeEnum.PC.getType(), null)));
        }
    }

    /**
     * (channel必在本地)登录成功，并更新状态
     */
    private void loginSuccess(Channel channel, User user, String token, String refreshToken) {
        // 更新上线列表
        NettyUtil.setAttr(channel, NettyUtil.UID, user.getId());
        ONLINE_UID_MAP.putIfAbsent(user.getId(), new CopyOnWriteArrayList<>());
        // 如果存在相同设备的连接，则先清空
        if (!ONLINE_UID_MAP.get(user.getId()).isEmpty()) {
            String loginType = NettyUtil.getAttr(channel, NettyUtil.LOGIN_TYPE);
            CopyOnWriteArrayList<Channel> channels = ONLINE_UID_MAP.get(user.getId());
            List<Channel> list = channels.stream().filter(c ->
                    Objects.equals(NettyUtil.getAttr(c, NettyUtil.LOGIN_TYPE), loginType)).toList();
            if(!list.isEmpty()) {
                channels.remove(list.getFirst());
            }
        }
        ONLINE_UID_MAP.get(user.getId()).add(channel);
        // 发送给对应的用户
        sendMsg(channel, WsAdapter.buildLoginSuccessResp(user, token, refreshToken, roleService.hasRole(user.getId(), RoleTypeEnum.CHAT_MANAGER)));
    }

    @Override
    public void scanLoginSuccess(LoginMessageDTO loginMessageDTO) {
        // 确认连接在该机器
        Channel channel = WAIT_LOGIN_MAP.getIfPresent(loginMessageDTO.getCode());
        if (Objects.isNull(channel)) {
            return;
        }
        User user = userDao.getById(loginMessageDTO.getCode());
        // 移除code
        WAIT_LOGIN_MAP.invalidate(loginMessageDTO.getCode());
        // 登录类型默认为PC
		LoginResultVO token = tokenService.createToken(loginMessageDTO.getUid(), LoginTypeEnum.PC.getType());
        // 用户登录
        loginSuccess(channel, user, token.getToken(), token.getRefreshToken());
        // 发送用户上线事件
        if (userCache.isOnline(user.getId())) {
            user.setLastOptTime(new Date());
            user.refreshIp(NettyUtil.getAttr(channel, NettyUtil.IP));
            applicationEventPublisher.publishEvent(new UserOnlineEvent(this, user));
        }
    }

    @Override
    public void scanSuccess(ScanSuccessMessageDTO scanSuccessMessageDTO) {
        Channel channel = WAIT_LOGIN_MAP.getIfPresent(scanSuccessMessageDTO.getCode());
        if (Objects.nonNull(channel)) {
            sendMsg(channel, WsAdapter.buildScanSuccessResp());
        }
    }

    // entrySet的值不是快照数据,但是它支持遍历，所以无所谓了，不用快照也行。
    @Override
    public void sendAll(WsBaseResp<?> wsBaseResp, Long skipUid) {
        ONLINE_WS_MAP.forEach((ip, channel) -> {
            if (Objects.equals(NettyUtil.getAttr(channel, NettyUtil.UID), skipUid)) {
                return;
            }
            threadPoolTaskExecutor.execute(() -> sendMsg(channel, wsBaseResp));
        });
    }

    @Override
    public void sendUser(WsBaseResp<?> wsBaseResp, Long uid) {
        CopyOnWriteArrayList<Channel> channels = ONLINE_UID_MAP.get(uid);
        if (CollectionUtil.isEmpty(channels)) {
            log.info("用户：{}不在线", uid);
            return;
        }
        channels.forEach(channel -> {
            log.info("发送uid");
            threadPoolTaskExecutor.execute(() -> sendMsg(channel, wsBaseResp));
        });
    }

    /**
     * 给本地channel发送消息
     *
     * @param channel 通道
     * @param wsBaseResp 消息
     */
    private void sendMsg(Channel channel, WsBaseResp<?> wsBaseResp) {
        channel.writeAndFlush(new TextWebSocketFrame(JSONUtil.toJsonStr(wsBaseResp)));
    }
}
