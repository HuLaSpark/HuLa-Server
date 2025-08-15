package com.luohuo.flex.test.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.luohuo.basic.base.service.impl.SuperServiceImpl;
import com.luohuo.basic.database.mybatis.conditions.Wraps;
import com.luohuo.basic.utils.TreeUtil;
import com.luohuo.flex.test.entity.DefGenTestTree;
import com.luohuo.flex.test.manager.DefGenTestTreeManager;
import com.luohuo.flex.test.service.DefGenTestTreeService;
import com.luohuo.flex.test.vo.query.DefGenTestTreePageQuery;

import java.util.List;

/**
 * <p>
 * 业务实现类
 * 测试树结构
 * </p>
 *
 * @author zuihou
 * @date 2022-04-20 00:28:30
 * @create [2022-04-20 00:28:30] [zuihou] [代码生成器生成]
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DefGenTestTreeServiceImpl extends SuperServiceImpl<DefGenTestTreeManager, Long, DefGenTestTree> implements DefGenTestTreeService {

    @Override
    public List<DefGenTestTree> findTree(DefGenTestTreePageQuery query) {
        List<DefGenTestTree> list = superManager.list(Wraps.<DefGenTestTree>lbQ().orderByAsc(DefGenTestTree::getSortValue));
        return TreeUtil.buildTree(list);
    }

}


