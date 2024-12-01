package com.hula.core.user.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.hula.common.config.ThreadPoolConfig;
import com.hula.common.constant.RedisKey;
import com.hula.common.enums.LoginTypeEnum;
import com.hula.common.event.UserOfflineEvent;
import com.hula.common.event.UserOnlineEvent;
import com.hula.core.user.dao.UserDao;
import com.hula.core.user.domain.entity.User;
import com.hula.core.user.domain.enums.RoleEnum;
import com.hula.core.user.domain.enums.WSBaseResp;
import com.hula.core.user.domain.vo.req.ws.WSAuthorize;
import com.hula.core.user.service.LoginService;
import com.hula.core.user.service.RoleService;
import com.hula.core.user.service.TokenService;
import com.hula.core.user.service.WebSocketService;
import com.hula.core.user.service.adapter.WSAdapter;
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
    public void handleLoginReq(Channel channel) {
        // 生成随机不重复的登录码,并将channel存在本地cache中
        Integer code = generateLoginCode(channel);
        // 请求微信接口，获取登录码地址
        WxMpQrCodeTicket wxMpQrCodeTicket = wxMpService.getQrcodeService().qrCodeCreateTmpTicket(code, (int) EXPIRE_TIME.getSeconds());
        // 返回给前端（channel必在本地）
        sendMsg(channel, WSAdapter.buildLoginResp(wxMpQrCodeTicket));
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
        // 保证每个IP只有一个连接
        String ip = NettyUtil.getAttr(channel, NettyUtil.IP);
        Channel oldChannel = ONLINE_WS_MAP.get(ip);
        if(Objects.nonNull(oldChannel)) {
            oldChannel.close();
            ONLINE_WS_MAP.remove(ip);
        }
        ONLINE_WS_MAP.put(ip, channel);
    }

    @Override
    public void removed(Channel channel) {
        Optional<Long> uid = Optional.ofNullable(NettyUtil.getAttr(channel, NettyUtil.UID));
        boolean offlineAll = offline(channel, uid);
        // 已登录用户断连,并且全下线成功
        if (uid.isPresent() && offlineAll) {
            User user = new User();
            user.setId(uid.get());
            user.setLastOptTime(new Date());
            applicationEventPublisher.publishEvent(new UserOfflineEvent(this, user));
        }
    }

    @Override
    public void authorize(Channel channel, WSAuthorize wsAuthorize) {
        // 校验token
        boolean verifySuccess = tokenService.verify(wsAuthorize.getToken());
        if (verifySuccess) {//用户校验成功给用户登录
            User user = userDao.getById(JwtUtils.getUidOrNull(wsAuthorize.getToken()));
            loginSuccess(channel, user, wsAuthorize.getToken());
        } else { // 让前端的token失效
            Long uid = JwtUtils.getUidOrNull(wsAuthorize.getToken());
            User user = User.builder().id(uid).build();
            user.refreshIp(null);
            sendMsg(channel, WSAdapter.buildInvalidateTokenResp(user));
        }
    }

    /**
     * (channel必在本地)登录成功，并更新状态
     */
    private void loginSuccess(Channel channel, User user, String token) {
        // 更新上线列表
        online(channel, user.getId());
        // 返回给用户登录成功
        boolean hasPower = roleService.hasPower(user.getId(), RoleEnum.CHAT_MANAGER);
        // 发送给对应的用户
        sendMsg(channel, WSAdapter.buildLoginSuccessResp(user, token, hasPower));
    }

    /**
     * 用户上线
     */
    private void online(Channel channel, Long uid) {
        NettyUtil.setAttr(channel, NettyUtil.UID, uid);
        ONLINE_UID_MAP.putIfAbsent(uid, new CopyOnWriteArrayList<>());
        // 如果存在相同设备的连接，则先清空
        if (!ONLINE_UID_MAP.get(uid).isEmpty()) {
            String loginType = NettyUtil.getAttr(channel, NettyUtil.LOGIN_TYPE);
            CopyOnWriteArrayList<Channel> channels = ONLINE_UID_MAP.get(uid);
            List<Channel> list = channels.stream().filter(c ->
                    NettyUtil.getAttr(c, NettyUtil.LOGIN_TYPE).equals(loginType)).toList();
            if(!list.isEmpty()) {
               channels.remove(list.getFirst());
            }
        }
        ONLINE_UID_MAP.get(uid).add(channel);
    }

    /**
     * 用户下线
     * return 是否全下线成功
     */
    private boolean offline(Channel channel, Optional<Long> uidOptional) {
        ONLINE_WS_MAP.remove((String)NettyUtil.getAttr(channel, NettyUtil.IP));
        if (uidOptional.isPresent()) {
            CopyOnWriteArrayList<Channel> channels = ONLINE_UID_MAP.get(uidOptional.get());
            if (CollectionUtil.isNotEmpty(channels)) {
                channels.removeIf(ch -> Objects.equals(ch, channel));
            }
            return CollectionUtil.isEmpty(ONLINE_UID_MAP.get(uidOptional.get()));
        }
        return true;
    }

    @Override
    public void scanLoginSuccess(Integer loginCode, Long uid) {
        // 确认连接在该机器
        Channel channel = WAIT_LOGIN_MAP.getIfPresent(loginCode);
        if (Objects.isNull(channel)) {
            return;
        }
        User user = userDao.getById(uid);
        // 移除code
        WAIT_LOGIN_MAP.invalidate(loginCode);
        // 创建token
        // TODO 登录类型待处理
        String token = tokenService.createToken(uid, LoginTypeEnum.PC);
        // 用户登录
        loginSuccess(channel, user, token);
        // 发送用户上线事件
        if (userCache.isOnline(user.getId())) {
            user.setLastOptTime(new Date());
            user.refreshIp(NettyUtil.getAttr(channel, NettyUtil.IP));
            applicationEventPublisher.publishEvent(new UserOnlineEvent(this, user));
        }
    }

    @Override
    public void scanSuccess(Integer loginCode) {
        Channel channel = WAIT_LOGIN_MAP.getIfPresent(loginCode);
        if (Objects.nonNull(channel)) {
            sendMsg(channel, WSAdapter.buildScanSuccessResp());
        }
    }

    // entrySet的值不是快照数据,但是它支持遍历，所以无所谓了，不用快照也行。
    @Override
    public void sendToAllOnline(WSBaseResp<?> wsBaseResp, Long skipUid) {
        ONLINE_WS_MAP.forEach((ip, channel) -> {
            if (Objects.equals(NettyUtil.getAttr(channel, NettyUtil.UID), skipUid)) {
                return;
            }
            threadPoolTaskExecutor.execute(() -> sendMsg(channel, wsBaseResp));
        });
    }

    @Override
    public void sendToAllOnline(WSBaseResp<?> wsBaseResp) {
        sendToAllOnline(wsBaseResp, null);
    }

    @Override
    public void sendToUid(WSBaseResp<?> wsBaseResp, Long uid) {
        CopyOnWriteArrayList<Channel> channels = ONLINE_UID_MAP.get(uid);
        if (CollectionUtil.isEmpty(channels)) {
            log.info("用户：{}不在线", uid);
            return;
        }
        channels.forEach(channel -> {
            threadPoolTaskExecutor.execute(() -> sendMsg(channel, wsBaseResp));
        });
    }


    /**
     * 给本地channel发送消息
     *
     * @param channel 通道
     * @param wsBaseResp 消息
     */
    private void sendMsg(Channel channel, WSBaseResp<?> wsBaseResp) {
        channel.writeAndFlush(new TextWebSocketFrame(JSONUtil.toJsonStr(wsBaseResp)));
    }
}
