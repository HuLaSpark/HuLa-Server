package com.luohuo.flex.base.manager.tenant;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.luohuo.basic.base.manager.SuperCacheManager;
import com.luohuo.basic.interfaces.echo.LoadService;
import com.luohuo.flex.base.entity.tenant.DefUser;
import com.luohuo.flex.base.vo.query.tenant.DefUserPageQuery;
import com.luohuo.flex.base.vo.query.tenant.DefUserResultVO;

import java.util.Collection;

/**
 * <p>
 * 通用业务层
 * 用户
 * </p>
 *
 * @author tangyh
 * @version v1.0
 * @date 2021/9/29 1:26 下午
 * @create [2021/9/29 1:26 下午 ] [tangyh] [初始创建]
 */
public interface DefUserManager extends SuperCacheManager<DefUser>, LoadService {

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
     * 根据用户名查询用户
	 * @param loginType 登录系统
     * @param username 用户名
     * @return
     */
    DefUser getUserByUsername(Integer loginType, String username);

    /**
     * 查找同一企业下的用户
     *
     * @param pageQuery 参数
     * @param page      分页参数
     * @return
     */
    IPage<DefUserResultVO> pageUser(DefUserPageQuery pageQuery, IPage<DefUser> page);


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
     * 根据手机号查询用户
     *
     * @param mobile 手机号
     * @return
     */
    DefUser getUserByMobile(Integer loginType, String mobile);

    /**
     * 清理缓存
     * @param list id或用户对象
     */
    void delUserCache(Collection<?> list);
}
