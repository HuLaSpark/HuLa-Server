package com.luohuo.flex.ai.controller.model;

import cn.hutool.core.util.ObjUtil;
import com.luohuo.flex.ai.common.pojo.PageResult;
import com.luohuo.flex.ai.controller.model.vo.chatRole.AiChatRolePageReqVO;
import com.luohuo.flex.ai.controller.model.vo.chatRole.AiChatRoleRespVO;
import com.luohuo.flex.ai.controller.model.vo.chatRole.AiChatRoleSaveMyReqVO;
import com.luohuo.flex.ai.controller.model.vo.chatRole.AiChatRoleSaveReqVO;
import com.luohuo.flex.ai.dal.model.AiChatRoleDO;
import com.luohuo.flex.ai.service.model.AiChatRoleService;
import com.luohuo.flex.ai.utils.BeanUtils;
import com.luohuo.basic.base.R;
import com.luohuo.basic.context.ContextUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.luohuo.basic.base.R.success;


@Tag(name = "管理后台 - AI 聊天角色")
@RestController
@RequestMapping("/chat-role")
@Validated
public class AiChatRoleController {

    @Resource
    private AiChatRoleService chatRoleService;

    @PostMapping("/create")
    @Operation(summary = "创建聊天角色")
    public R<Long> createChatRole(@Valid @RequestBody AiChatRoleSaveReqVO createReqVO) {
        if (createReqVO.getPublicStatus() == null || Boolean.FALSE.equals(createReqVO.getPublicStatus())) {
            AiChatRoleSaveMyReqVO myReqVO = BeanUtils.toBean(createReqVO, AiChatRoleSaveMyReqVO.class);
            return success(chatRoleService.createChatRoleMy(myReqVO, ContextUtil.getUid()));
        }
        // 管理员创建公开角色
        return success(chatRoleService.createChatRole(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新聊天角色")
    public R<Boolean> updateChatRole(@Valid @RequestBody AiChatRoleSaveReqVO updateReqVO) {
        AiChatRoleDO chatRole = chatRoleService.getChatRole(updateReqVO.getId());
        if (chatRole == null) {
            return success(false);
        }

        Long uid = ContextUtil.getUid();
		if (ObjUtil.notEqual(chatRole.getUserId(), uid)) {
			return success(false);
		}
		chatRoleService.updateChatRoleMy(updateReqVO, uid);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除聊天角色")
    @Parameter(name = "id", description = "编号", required = true)
    public R<Boolean> deleteChatRole(@RequestParam("id") Long id) {
        AiChatRoleDO chatRole = chatRoleService.getChatRole(id);
        if (chatRole == null) {
            return success(false);
        }

        Long uid = ContextUtil.getUid();
        if (ObjUtil.notEqual(chatRole.getUserId(), uid)) {
            return success(false);
        }

		chatRoleService.deleteChatRoleMy(id, uid);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得聊天角色")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public R<AiChatRoleRespVO> getChatRole(@RequestParam("id") Long id) {
        AiChatRoleDO chatRole = chatRoleService.getChatRole(id);
        return success(BeanUtils.toBean(chatRole, AiChatRoleRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得聊天角色分页（包含系统公开角色和用户私有角色）")
    public R<PageResult<AiChatRoleRespVO>> getChatRolePage(@Valid AiChatRolePageReqVO pageReqVO) {
        PageResult<AiChatRoleDO> pageResult = chatRoleService.getChatRoleMyPage(pageReqVO, ContextUtil.getUid());
        return success(BeanUtils.toBean(pageResult, AiChatRoleRespVO.class));
    }

    @GetMapping("/category-list")
    @Operation(summary = "获得聊天角色的分类列表")
    public R<List<Map<String, String>>> getChatRoleCategoryList() {
        return success(chatRoleService.getChatRoleCategoryList());
    }

}
