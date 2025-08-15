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

import static com.luohuo.basic.base.R.success;


@Tag(name = "管理后台 - AI 聊天角色")
@RestController
@RequestMapping("/chat-role")
@Validated
public class AiChatRoleController {

    @Resource
    private AiChatRoleService chatRoleService;

    @GetMapping("/my-page")
    @Operation(summary = "获得【我的】聊天角色分页")
    public R<PageResult<AiChatRoleRespVO>> getChatRoleMyPage(@Valid AiChatRolePageReqVO pageReqVO) {
        PageResult<AiChatRoleDO> pageResult = chatRoleService.getChatRoleMyPage(pageReqVO, ContextUtil.getUid());
        return success(BeanUtils.toBean(pageResult, AiChatRoleRespVO.class));
    }

    @GetMapping("/get-my")
    @Operation(summary = "获得【我的】聊天角色")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public R<AiChatRoleRespVO> getChatRoleMy(@RequestParam("id") Long id) {
        AiChatRoleDO chatRole = chatRoleService.getChatRole(id);
        if (ObjUtil.notEqual(chatRole.getUserId(), ContextUtil.getUid())) {
            return success(null);
        }
        return success(BeanUtils.toBean(chatRole, AiChatRoleRespVO.class));
    }

    @PostMapping("/create-my")
    @Operation(summary = "创建【我的】聊天角色")
    public R<Long> createChatRoleMy(@Valid @RequestBody AiChatRoleSaveMyReqVO createReqVO) {
        return success(chatRoleService.createChatRoleMy(createReqVO, ContextUtil.getUid()));
    }

    @PutMapping("/update-my")
    @Operation(summary = "更新【我的】聊天角色")
    public R<Boolean> updateChatRoleMy(@Valid @RequestBody AiChatRoleSaveMyReqVO updateReqVO) {
        chatRoleService.updateChatRoleMy(updateReqVO, ContextUtil.getUid());
        return success(true);
    }

    @DeleteMapping("/delete-my")
    @Operation(summary = "删除【我的】聊天角色")
    @Parameter(name = "id", description = "编号", required = true)
    public R<Boolean> deleteChatRoleMy(@RequestParam("id") Long id) {
        chatRoleService.deleteChatRoleMy(id, ContextUtil.getUid());
        return success(true);
    }

    @GetMapping("/category-list")
    @Operation(summary = "获得聊天角色的分类列表")
    public R<List<String>> getChatRoleCategoryList() {
        return success(chatRoleService.getChatRoleCategoryList());
    }

    // ========== 角色管理 ==========

    @PostMapping("/create")
    @Operation(summary = "创建聊天角色")
    public R<Long> createChatRole(@Valid @RequestBody AiChatRoleSaveReqVO createReqVO) {
        return success(chatRoleService.createChatRole(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新聊天角色")
    public R<Boolean> updateChatRole(@Valid @RequestBody AiChatRoleSaveReqVO updateReqVO) {
        chatRoleService.updateChatRole(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除聊天角色")
    @Parameter(name = "id", description = "编号", required = true)
    public R<Boolean> deleteChatRole(@RequestParam("id") Long id) {
        chatRoleService.deleteChatRole(id);
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
    @Operation(summary = "获得聊天角色分页")
    public R<PageResult<AiChatRoleRespVO>> getChatRolePage(@Valid AiChatRolePageReqVO pageReqVO) {
        PageResult<AiChatRoleDO> pageResult = chatRoleService.getChatRolePage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, AiChatRoleRespVO.class));
    }

}
