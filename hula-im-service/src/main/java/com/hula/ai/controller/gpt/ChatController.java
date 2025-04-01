package com.hula.ai.controller.gpt;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hula.ai.gpt.pojo.param.ChatParam;
import com.hula.ai.gpt.pojo.vo.ChatVO;
import com.hula.ai.gpt.service.IChatService;
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
 *  聊天摘要接口
 *
 * @author: 云裂痕
 * @date: 2023-04-28
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
@RestController(value = "ChatAiListController")
@RequestMapping("/gpt/chat")
public class ChatController {
    @Resource
    private IChatService chatService;

    /**
     * 查询聊天摘要分页列表
     *
     * @author: 云裂痕
     * @date: 2023-04-28
     * @version: 1.0.0
     */
    @GetMapping("/page")
    public ApiResult<IPage<ChatVO>> pageChat(@RequestParam ChatParam param) {
        return ApiResult.success(chatService.pageChat(param));
    }

    /**
     * 查询聊天摘要列表
     *
     * @author: 云裂痕
     * @date: 2023-04-28
     * @version: 1.0.0
     */
    @PostMapping("/list")
    public ApiResult<List<ChatVO>> listChat(@RequestBody ChatParam param) {
        return ApiResult.success(chatService.listChat(param));
    }

    /**
     * 获取聊天摘要详细信息
     *
     * @author: 云裂痕
     * @date: 2023-04-28
     * @version: 1.0.0
     */
    @GetMapping(value = "/{id}")
    public ApiResult<ChatVO> getChatById(@PathVariable("id") Long id) {
        return ApiResult.success(chatService.getChatById(id));
    }

    /**
     * 批量删除聊天摘要
     *
     * @author: 云裂痕 false
     * @date: 2023-04-28
     * @version: 1.0.0
     */
    @PostMapping("/{ids}")
    public ApiResult removeChatByIds(@PathVariable List<Long> ids) {
        return ApiResult.success(chatService.removeChatByIds(ids));
    }

}
