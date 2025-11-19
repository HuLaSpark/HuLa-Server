package com.luohuo.flex.oauth.biz;

import cn.hutool.core.bean.BeanUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.luohuo.basic.context.ContextUtil;
import com.luohuo.flex.base.entity.tenant.DefUser;
import com.luohuo.flex.base.entity.user.BaseEmployee;
import com.luohuo.flex.base.service.tenant.DefUserService;
import com.luohuo.flex.base.service.user.BaseEmployeeService;
import com.luohuo.flex.base.vo.result.user.BaseEmployeeResultVO;
import com.luohuo.flex.oauth.vo.result.DefUserInfoResultVO;
import com.luohuo.flex.base.entity.application.DefApplication;
import com.luohuo.flex.base.service.application.DefApplicationService;
import com.luohuo.flex.base.vo.result.application.DefApplicationResultVO;

/**
 * 用户大业务
 *
 * @author 乾乾
 * @date 2021/10/28 13:09
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class OauthUserBiz {
    private final BaseEmployeeService baseEmployeeService;
    private final DefUserService defUserService;
    private final DefApplicationService defApplicationService;

    public DefUserInfoResultVO getUserById(Long id) {
        // 查默认库
        DefUser defUser = defUserService.getByIdCache(id);
        if (defUser == null) {
            return null;
        }

        // 用户信息
        DefUserInfoResultVO resultVO = new DefUserInfoResultVO();
        BeanUtil.copyProperties(defUser, resultVO);

        resultVO.setAvatar(defUser.getAvatar());
        Long uid = ContextUtil.getUid();
        resultVO.setUid(uid);

        //查 租户库
        BaseEmployee employee = baseEmployeeService.getById(uid);
        resultVO.setBaseEmployee(BeanUtil.toBean(employee, BaseEmployeeResultVO.class));

        DefApplication defApplication = defApplicationService.getDefApp(id);
        resultVO.setDefApplication(BeanUtil.toBean(defApplication, DefApplicationResultVO.class));
        return resultVO;
    }
}
