package com.luohuo.flex.im.core.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.luohuo.flex.im.domain.entity.UserPrivacy;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPrivacyMapper extends BaseMapper<UserPrivacy> {
    
    /**
     * 检查用户是否允许临时会话
     */
    @Select("SELECT allow_temp_session FROM im_user_privacy WHERE uid = #{uid}")
    Boolean checkAllowTempSession(@Param("uid") Long uid);
    
    /**
     * 检查用户是否是私密账号
     */
    @Select("SELECT is_private FROM im_user_privacy WHERE uid = #{uid}")
    Boolean checkIsPrivate(@Param("uid") Long uid);
}