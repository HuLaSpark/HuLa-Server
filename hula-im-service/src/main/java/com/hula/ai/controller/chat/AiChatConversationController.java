package com.hula.ai.controller.chat;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import com.hula.ai.common.pojo.PageResult;
import com.hula.ai.controller.chat.vo.conversation.AiChatConversationCreateMyReqVO;
import com.hula.ai.controller.chat.vo.conversation.AiChatConversationPageReqVO;
import com.hula.ai.controller.chat.vo.conversation.AiChatConversationRespVO;
import com.hula.ai.controller.chat.vo.conversation.AiChatConversationUpdateMyReqVO;
import com.hula.ai.dal.chat.AiChatConversationDO;
import com.hula.ai.service.chat.AiChatConversationService;
import com.hula.ai.service.chat.AiChatMessageService;
import com.hula.ai.utils.BeanUtils;
import com.hula.domain.vo.res.ApiResult;
import com.hula.utils.RequestHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.hula.ai.common.pojo.CommonResult.success;
import static com.hula.common.utils.CollectionUtils.convertList;

@Tag(name = "管理后台 - AI 聊天对话")
@RestController
@RequestMapping("/ai/chat/conversation")
@Validated
public class AiChatConversationController {

    @Resource
    private AiChatConversationService chatConversationService;
    @Resource
    private AiChatMessageService chatMessageService;

    @PostMapping("/create-my")
    @Operation(summary = "创建【我的】聊天对话")
    public ApiResult<Long> createChatConversationMy(@RequestBody @Valid AiChatConversationCreateMyReqVO createReqVO) {
        return success(chatConversationService.createChatConversationMy(createReqVO, RequestHolder.get().getUid()));
    }

    @PutMapping("/update-my")
    @Operation(summary = "更新【我的】聊天对话")
    public ApiResult<Boolean> updateChatConversationMy(@RequestBody @Valid AiChatConversationUpdateMyReqVO updateReqVO) {
        chatConversationService.updateChatConversationMy(updateReqVO, RequestHolder.get().getUid());
        return success(true);
    }

    @GetMapping("/my-list")
    @Operation(summary = "获得【我的】聊天对话列表")
    public ApiResult<List<AiChatConversationRespVO>> getChatConversationMyList() {
        List<AiChatConversationDO> list = chatConversationService.getChatConversationListByUserId(RequestHolder.get().getUid());
        return success(BeanUtils.toBean(list, AiChatConversationRespVO.class));
    }

    @GetMapping("/get-my")
    @Operation(summary = "获得【我的】聊天对话")
    @Parameter(name = "id", required = true, description = "对话编号", example = "1024")
    public ApiResult<AiChatConversationRespVO> getChatConversationMy(@RequestParam("id") Long id) {
        AiChatConversationDO conversation = chatConversationService.getChatConversation(id);
        if (conversation != null && ObjUtil.notEqual(conversation.getUserId(), RequestHolder.get().getUid())) {
            conversation = null;
        }
        return success(BeanUtils.toBean(conversation, AiChatConversationRespVO.class));
    }

    @DeleteMapping("/delete-my")
    @Operation(summary = "删除聊天对话")
    @Parameter(name = "id", required = true, description = "对话编号", example = "1024")
    public ApiResult<Boolean> deleteChatConversationMy(@RequestParam("id") Long id) {
        chatConversationService.deleteChatConversationMy(id, RequestHolder.get().getUid());
        return success(true);
    }

    @DeleteMapping("/delete-by-unpinned")
    @Operation(summary = "删除未置顶的聊天对话")
    public ApiResult<Boolean> deleteChatConversationMyByUnpinned() {
        chatConversationService.deleteChatConversationMyByUnpinned(RequestHolder.get().getUid());
        return success(true);
    }

    // ========== 对话管理 ==========

    @GetMapping("/page")
    @Operation(summary = "获得对话分页", description = "用于【对话管理】菜单")
    public ApiResult<PageResult<AiChatConversationRespVO>> getChatConversationPage(AiChatConversationPageReqVO pageReqVO) {
        PageResult<AiChatConversationDO> pageResult = chatConversationService.getChatConversationPage(pageReqVO);
        if (CollUtil.isEmpty(pageResult.getList())) {
            return success(PageResult.empty());
        }
        // 拼接关联数据
        Map<Long, Integer> messageCountMap = chatMessageService.getChatMessageCountMap(
                convertList(pageResult.getList(), AiChatConversationDO::getId));
        return success(BeanUtils.toBean(pageResult, AiChatConversationRespVO.class,
                conversation -> conversation.setMessageCount(messageCountMap.getOrDefault(conversation.getId(), 0))));
    }

    @Operation(summary = "管理员删除对话")
    @DeleteMapping("/delete-by-admin")
    @Parameter(name = "id", required = true, description = "对话编号", example = "1024")
    public ApiResult<Boolean> deleteChatConversationByAdmin(@RequestParam("id") Long id) {
        chatConversationService.deleteChatConversationByAdmin(id);
        return success(true);
    }

}
