//package com.luohuo.flex.gateway.service;
//
//import cn.dev33.satoken.stp.StpInterface;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//import com.luohuo.flex.oauth.biz.StpInterfaceBiz;
//
//import java.util.List;
//
///**
// * sa-token 权限网关实现
// * @author tangyh
// * @since 2024/8/6 21:46
// */
//@Component
//@Slf4j
//@RequiredArgsConstructor
//public class StpInterfaceServiceImpl implements StpInterface {
//    private final StpInterfaceBiz stpInterfaceBiz;
//
//    @Override
//    public List<String> getPermissionList(Object loginId, String loginType) {
//        return stpInterfaceBiz.getPermissionList();
//    }
//
//    @Override
//    public List<String> getRoleList(Object loginId, String loginType) {
//        return stpInterfaceBiz.getRoleList();
//    }
//}
