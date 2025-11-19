package com.luohuo.flex.ai.controller.chat;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import com.luohuo.basic.context.ContextUtil;
import com.luohuo.flex.ai.common.pojo.PageResult;
import com.luohuo.flex.ai.controller.chat.vo.conversation.AiDelReqVO;
import com.luohuo.flex.ai.controller.chat.vo.message.AiChatMessagePageReqVO;
import com.luohuo.flex.ai.controller.chat.vo.message.AiChatMessageRespVO;
import com.luohuo.flex.ai.controller.chat.vo.message.AiChatMessageSendReqVO;
import com.luohuo.flex.ai.controller.chat.vo.message.AiChatMessageSendRespVO;
import com.luohuo.flex.ai.dal.chat.AiChatConversationDO;
import com.luohuo.flex.ai.dal.chat.AiChatMessageDO;
import com.luohuo.flex.ai.dal.knowledge.AiKnowledgeDocumentDO;
import com.luohuo.flex.ai.dal.knowledge.AiKnowledgeSegmentDO;
import com.luohuo.flex.ai.dal.model.AiChatRoleDO;
import com.luohuo.flex.ai.service.chat.AiChatConversationService;
import com.luohuo.flex.ai.service.chat.AiChatMessageService;
import com.luohuo.flex.ai.service.knowledge.AiKnowledgeDocumentService;
import com.luohuo.flex.ai.service.knowledge.AiKnowledgeSegmentService;
import com.luohuo.flex.ai.service.model.AiChatRoleService;
import com.luohuo.flex.ai.utils.BeanUtils;
import com.luohuo.flex.ai.utils.MapUtils;
import com.luohuo.basic.base.R;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.luohuo.basic.base.R.success;
import static com.luohuo.basic.utils.collection.CollectionUtils.*;


@Tag(name = "管理后台 - 聊天消息")
@RestController
@RequestMapping("/chat/message")
@Slf4j
public class AiChatMessageController {

    @Resource
    private AiChatMessageService chatMessageService;
    @Resource
    private AiChatConversationService chatConversationService;
    @Resource
    private AiChatRoleService chatRoleService;
    @Resource
    private AiKnowledgeSegmentService knowledgeSegmentService;
    @Resource
    private AiKnowledgeDocumentService knowledgeDocumentService;

    @Operation(summary = "发送消息（段式）", description = "一次性返回，响应较慢")
    @PostMapping("/send")
    public R<AiChatMessageSendRespVO> sendMessage(@Valid @RequestBody AiChatMessageSendReqVO sendReqVO) {
        return success(chatMessageService.sendMessage(sendReqVO, ContextUtil.getUid()));
    }

    @Operation(summary = "发送消息（流式）", description = "流式返回，响应较快")
    @PostMapping(value = "/send-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<R<AiChatMessageSendRespVO>> sendChatMessageStream(@Valid @RequestBody AiChatMessageSendReqVO sendReqVO) {
        return chatMessageService.sendChatMessageStream(sendReqVO, ContextUtil.getUid());
    }

    @Operation(summary = "保存生成内容消息", description = "用于音频、图片、视频等生成功能，不调用 AI 模型。消息类型会根据模型类型自动确定")
    @PostMapping("/save-generated-content")
    public R<AiChatMessageSendRespVO> saveGeneratedContent(
            @RequestParam("conversationId") Long conversationId,
            @RequestParam("prompt") String prompt,
            @RequestParam("generatedContent") String generatedContent,
            @RequestParam(value = "msgType", required = false) @Deprecated Integer msgType) {
        return success(chatMessageService.saveGeneratedContent(conversationId, prompt, generatedContent, ContextUtil.getUid(), msgType));
    }

    @Operation(summary = "获得指定对话的消息列表")
    @GetMapping("/list-by-conversation-id")
    @Parameter(name = "conversationId", required = true, description = "对话编号", example = "1024")
    public R<List<AiChatMessageRespVO>> getChatMessageListByConversationId(@RequestParam("conversationId") Long conversationId) {
        AiChatConversationDO conversation = chatConversationService.getChatConversation(conversationId);
        if (conversation == null || ObjUtil.notEqual(conversation.getUserId(), ContextUtil.getUid())) {
            return success(Collections.emptyList());
        }
        // 1. 获取消息列表
        List<AiChatMessageDO> messageList = chatMessageService.getChatMessageListByConversationId(conversationId);
        if (CollUtil.isEmpty(messageList)) {
            return success(Collections.emptyList());
        }

        // 2. 拼接数据，主要是知识库段落信息
        Map<Long, AiKnowledgeSegmentDO> segmentMap = knowledgeSegmentService.getKnowledgeSegmentMap(convertListByFlatMap(messageList,
                message -> CollUtil.isEmpty(message.getSegmentIds()) ? null : message.getSegmentIds().stream()));
        Map<Long, AiKnowledgeDocumentDO> documentMap = knowledgeDocumentService.getKnowledgeDocumentMap(
                convertList(segmentMap.values(), AiKnowledgeSegmentDO::getDocumentId));
        List<AiChatMessageRespVO> messageVOList = BeanUtils.toBean(messageList, AiChatMessageRespVO.class);
        for (int i = 0; i < messageList.size(); i++) {
            AiChatMessageDO message = messageList.get(i);
            if (CollUtil.isEmpty(message.getSegmentIds())) {
                continue;
            }
            // 设置知识库段落信息
            messageVOList.get(i).setSegments(convertList(message.getSegmentIds(), segmentId -> {
                AiKnowledgeSegmentDO segment = segmentMap.get(segmentId);
                if (segment == null) {
                    return null;
                }
                AiKnowledgeDocumentDO document = documentMap.get(segment.getDocumentId());
                if (document == null) {
                    return null;
                }
                return new AiChatMessageRespVO.KnowledgeSegment().setId(segment.getId()).setContent(segment.getContent())
                        .setDocumentId(segment.getDocumentId()).setDocumentName(document.getName());
            }));
        }
        return success(messageVOList);
    }

    @Operation(summary = "删除消息")
    @DeleteMapping("/delete")
    @Parameter(name = "id", required = true, description = "消息编号", example = "1024")
    public R<Boolean> deleteChatMessage(@RequestParam("id") Long id) {
        chatMessageService.deleteChatMessage(id, ContextUtil.getUid());
        return success(true);
    }

    @Operation(summary = "删除指定对话的消息")
    @DeleteMapping("/delete-by-conversation-id")
    @Parameter(name = "conversationId", required = true, description = "对话编号", example = "1024")
    public R<Boolean> deleteChatMessageByConversationId(@RequestBody AiDelReqVO reqVOS) {
        chatMessageService.deleteChatMessageByConversationId(reqVOS, ContextUtil.getUid());
        return success(true);
    }

    // ========== 对话管理 ==========
    @GetMapping("/page")
    @Operation(summary = "获得消息分页", description = "用于【对话管理】菜单")
    public R<PageResult<AiChatMessageRespVO>> getChatMessagePage(AiChatMessagePageReqVO pageReqVO) {
        PageResult<AiChatMessageDO> pageResult = chatMessageService.getChatMessagePage(pageReqVO);
        if (CollUtil.isEmpty(pageResult.getList())) {
            return success(PageResult.empty());
        }
        // 拼接数据
        Map<Long, AiChatRoleDO> roleMap = chatRoleService.getChatRoleMap(
                convertSet(pageResult.getList(), AiChatMessageDO::getRoleId));
        return success(BeanUtils.toBean(pageResult, AiChatMessageRespVO.class,
                respVO -> MapUtils.findAndThen(roleMap, respVO.getRoleId(),
                        role -> respVO.setRoleName(role.getName()))));
    }

    @Operation(summary = "管理员删除消息")
    @DeleteMapping("/delete-by-admin")
    @Parameter(name = "id", required = true, description = "消息编号", example = "1024")
    public R<Boolean> deleteChatMessageByAdmin(@RequestParam("id") Long id) {
        chatMessageService.deleteChatMessageByAdmin(id);
        return success(true);
    }

}
