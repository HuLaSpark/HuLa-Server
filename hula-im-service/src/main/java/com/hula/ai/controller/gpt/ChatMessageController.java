package com.hula.ai.controller.gpt;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hula.ai.gpt.pojo.entity.ChatMessage;
import com.hula.ai.gpt.pojo.param.ChatMessageParam;
import com.hula.ai.gpt.pojo.param.ChatParam;
import com.hula.ai.gpt.pojo.vo.ChatMessageVO;
import com.hula.ai.gpt.service.IChatMessageService;
import com.hula.common.domain.vo.res.CursorPageBaseResp;
import com.hula.domain.vo.res.ApiResult;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 *  对话消息接口
 *
 * @author: 云裂痕
 * @date: 2023-04-28
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
@RestController
@RequestMapping("/gpt/chat-message")
public class ChatMessageController {
    @Resource
    private IChatMessageService chatMessageService;

    /**
     * 查询对话消息分页列表
     *
     * @author: 云裂痕
     * @date: 2023-04-28
     * @version: 1.0.0
     */
    @GetMapping("/page")
    public ApiResult<IPage<ChatMessageVO>> pageChatMessage(@RequestParam ChatMessageParam param) {
        return ApiResult.success(chatMessageService.pageChatMessage(param));
    }

    /**
     * 查询对话消息列表
     *
     * @author: 云裂痕
     * @date: 2023-04-28
     * @version: 1.0.0
     */
    @PostMapping("/list")
    public ApiResult<List<ChatMessageVO>> listChatMessage(@RequestBody ChatParam param) {
        return ApiResult.success(chatMessageService.listChatMessage(param));
    }

    /**
     * 获取对话消息详细信息
     *
     * @author: 云裂痕
     * @date: 2023-04-28
     * @version: 1.0.0
     */
    @GetMapping(value = "/{id}")
    public ApiResult<ChatMessageVO> getChatMessageById(@PathVariable("id") Long id) {
        return ApiResult.success(chatMessageService.getChatMessageById(id));
    }

    /**
     * 批量删除对话消息
     *
     * @author: 云裂痕 false
     * @date: 2023-04-28
     * @version: 1.0.0
     */
    @PostMapping("/{ids}")
    public ApiResult removeChatMessageByIds(@PathVariable List<Long> ids) {
        return ApiResult.returnResult("删除", chatMessageService.removeChatMessageByIds(ids));
    }

}
