package com.luohuo.flex.base.service.tenant;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.luohuo.basic.base.request.PageParams;
import com.luohuo.basic.base.service.SuperCacheService;
import com.luohuo.flex.base.entity.tenant.DefUser;
import com.luohuo.flex.base.vo.query.tenant.DefUserPageQuery;
import com.luohuo.flex.base.vo.query.tenant.DefUserResultVO;
import com.luohuo.flex.base.vo.update.tenant.DefUserAvatarUpdateVO;
import com.luohuo.flex.base.vo.update.tenant.DefUserBaseInfoUpdateVO;
import com.luohuo.flex.base.vo.update.tenant.DefUserEmailUpdateVO;
import com.luohuo.flex.base.vo.update.tenant.DefUserMobileUpdateVO;
import com.luohuo.flex.base.vo.update.tenant.DefUserPasswordResetVO;
import com.luohuo.flex.base.vo.update.tenant.DefUserPasswordUpdateVO;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * 业务接口
 * 用户
 * </p>
 *
 * @author 乾乾
 * @date 2021-10-09
 */
public interface DefUserService extends SuperCacheService<Long, DefUser> {
    /**
     * 根据id查询待回显参数
     *
     * @param ids 唯一键（可能不是主键ID)
     * @return
     */
    Map<Serializable, Object> findByIds(Set<Serializable> ids);

    /**
     * 根据id查询
     *
     * @param idList 用户id
     * @return
     */
    default List<DefUser> listByIds(Collection<? extends Serializable> idList) {
        return getSuperManager().listByIds(idList);
    }

    /**
     * 查询用户id
     *
     * @param pageQuery
     * @return
     */
    List<Long> findUserIdList(DefUserPageQuery pageQuery);

    /**
     * 检测用户名是否可用
     *
     * @param username 用户名
     * @param id       用户id
     * @return boolean
     * @author tangyh
     * @date 2021/10/10 12:22 下午
     * @create [2021/10/10 12:22 下午 ] [tangyh] [初始创建]
     */
    boolean checkUsername(String username, Long id);

    /**
     * 检测 邮箱 是否可用
     *
     * @param email 邮箱
     * @param id    用户id
     * @return boolean
     * @author tangyh
     * @date 2021/10/10 12:23 下午
     * @create [2021/10/10 12:23 下午 ] [tangyh] [初始创建]
     */
    boolean checkEmail(String email, Long id);

    /**
     * 检测 手机号 是否可用
     *
     * @param mobile 手机号
     * @param id     用户id
     * @return boolean
     * @author tangyh
     * @date 2021/10/10 12:24 下午
     * @create [2021/10/10 12:24 下午 ] [tangyh] [初始创建]
     */
    boolean checkMobile(String mobile, Long id);

    /**
     * 检测 身份证 是否可用
     *
     * @param idCard 身份证
     * @param id     用户id
     * @return boolean
     * @author tangyh
     * @date 2021/10/10 12:24 下午
     * @create [2021/10/10 12:24 下午 ] [tangyh] [初始创建]
     */
    boolean checkIdCard(String idCard, Long id);

    /**
     * 根据邮箱查询用户
     *
     * @param email 邮箱
     * @return
     */
    DefUser getUserByEmail(Integer loginType, String email);

    /**
     * 根据用户名查询用户
	 * @param loginType 登录系统
     * @param username 用户名
     * @return
     */
    DefUser getUserByUsername(Integer loginType, String username);

    /**
     * 根据手机号查询用户
     *
     * @param mobile 手机号
     * @return
     */
    DefUser getUserByMobile(Integer loginType, String mobile);

    /**
     * 重置密码
     *
     * @param data 重置密码信息
     * @return 是否重置成功
     */
    Boolean resetPassword(DefUserPasswordResetVO data);

    /**
     * 修改状态
     *
     * @param id    用户id
     * @param state 用户状态
     * @return 是否修改成功
     */
    Boolean updateState(Long id, Boolean state);

    /**
     * 修改头像
     *
     * @param data 头像信息
     * @return 是否修改成功
     */
    Boolean updateAvatar(DefUserAvatarUpdateVO data);

    /**
     * 修改密码
     *
     * @param data 密码信息
     * @return 是否修改成功
     */
    Boolean updatePassword(DefUserPasswordUpdateVO data);

    /**
     * 修改手机
     *
     * @param data 信息
     * @return 是否修改成功
     */
    Boolean updateMobile(DefUserMobileUpdateVO data);

    /**
     * 修改邮箱
     *
     * @param data 信息
     * @return 是否修改成功
     */
    Boolean updateEmail(DefUserEmailUpdateVO data);

    /**
     * 修改个人信息
     *
     * @param data 个人信息
     * @return 是否修改成功
     */
    Boolean updateBaseInfo(DefUserBaseInfoUpdateVO data);


    /**
     * 重置密码错误次数
     *
     * @param id 用户id
     * @return 重置了多少行
     */
    int resetPassErrorNum(Long id);

    /**
     * 修改输错密码的次数
     *
     * @param id 用户Id
     */
    void incrPasswordErrorNumById(Long id);

    /**
     * 注册
     *
     * @param defUser
     * @return
     */
    String register(DefUser defUser);

    /**
     * 注册
     *
     * @param defUser
     * @return
     */
    String registerByEmail(DefUser defUser);

    /**
     * 查找同一企业下的用户
     *
     * @param params
     * @return
     */
    IPage<DefUserResultVO> pageUser(PageParams<DefUserPageQuery> params);

    /**
     * 邀请员工进入企业前精确查询用户
     *
     * @param params
     * @return
     */
    List<DefUserResultVO> queryUser(DefUserPageQuery params);
}
