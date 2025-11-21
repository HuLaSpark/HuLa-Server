package com.luohuo.basic.database.mybatis.typehandler;


import com.baomidou.mybatisplus.core.enums.SqlLike;
import org.apache.ibatis.type.Alias;

/**
 * 仅仅用于like查询
 *
 * @author 乾乾
 */
@Alias("leftLike")
public class LeftLikeTypeHandler extends BaseLikeTypeHandler {
    public LeftLikeTypeHandler() {
        super(SqlLike.LEFT);
    }
}
