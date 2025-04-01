package com.hula.ai.controller.app;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.hula.ai.client.model.command.ChatCommand;
import com.hula.ai.common.enums.StatusEnum;
import com.hula.ai.gpt.pojo.param.AgreementParam;
import com.hula.ai.gpt.pojo.vo.AgreementVO;
import com.hula.ai.gpt.service.IAgreementService;
import com.hula.ai.gpt.service.IAssistantService;
import com.hula.ai.gpt.service.IAssistantTypeService;
import com.hula.ai.llm.base.service.LLMService;
import com.hula.domain.vo.res.ApiResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Random;

/**
 * 获取小程序基础信息接口
 *
 * @author: 云裂痕
 * @date: 2023/5/4
 * @version: 1.0.0
 * 得其道
 * 乾乾
 */
@RestController
@RequestMapping("/app/api")
public class AppApiController {
    @Autowired
    private IAssistantTypeService assistantTypeService;
    @Autowired
    private IAssistantService assistantService;
    @Autowired
    private IAgreementService contentService;
    @Autowired
    private LLMService llmService;

    /**
     * 获取协议信息
     *
     * @author: 云裂痕
     * @date: 2023/1/9
     * @version: 1.0.0
     */
    @GetMapping("/content/agreement/{type}")
    public ApiResult getAgreement(@PathVariable("type") Integer type) {
        List<AgreementVO> contents = contentService.listContent(new AgreementParam(StatusEnum.ENABLED.getValue(), type));
        if (CollUtil.isEmpty(contents)) {
            return ApiResult.success();
        }
        return ApiResult.success(contents.get(0));
    }

    /**
     * 获取系统内容信息
     *
     * @author: 云裂痕
     * @date: 2023/1/9
     * @version: 1.0.0
     */
    @GetMapping("/content/{type}")
    public ApiResult listContent(@PathVariable("type") Integer type) {
        return ApiResult.success(contentService.listContent(new AgreementParam(StatusEnum.ENABLED.getValue(), type)));
    }

    /**
     * 获取内容详情
     *
     * @author: 云裂痕
     * @date: 2023/1/9
     * @version: 1.0.0
     */
    @GetMapping("/content/detail/{id}")
    public ApiResult getContent(@PathVariable("id") Long id) {
        return ApiResult.success(contentService.getContentById(id));
    }

    /**
     * 获取Ai分类
     *
     * @author: 云裂痕
     * @date: 2023/1/9
     * @version: 1.0.0
     */
    @GetMapping("/assistant/type")
    public ApiResult listAssistantType() {
		AgreementParam param = new AgreementParam();
		param.setStatus(StatusEnum.ENABLED.getValue());
        return ApiResult.success(assistantTypeService.listAssistantType(param));
    }

    /**
     * 获取Ai助手
     *
     * @author: 云裂痕
     * @date: 2023/1/9
     * @version: 1.0.0
     */
    @GetMapping("/assistant")
    public ApiResult listAssistantByType(@RequestParam AgreementParam param) {
		param.setStatus(StatusEnum.ENABLED.getValue());
        return ApiResult.success(assistantService.listAssistantByApp(param));
    }

    /**
     * 获取Ai助手
     *
     * @author: 云裂痕
     * @date: 2023/1/9
     * @version: 1.0.0
     */
    @GetMapping("/assistant/{id}")
    public ApiResult getAssistantById(@PathVariable Long id) {
        return assistantService.getAssistantById(id);
    }

    /**
     * 随机获取Ai助手
     *
     * @author: 乾乾
     * @date: 2025/03/07
     */
    @GetMapping("/assistant/random")
    public ApiResult listAssistant(AgreementParam param) {
		param.setStatus(StatusEnum.ENABLED.getValue());
		if(ObjectUtil.isNull(param.getSize()) || param.getSize() == 0){
			param.setSize(3);
		}
		param.setCurrent(new Random().nextInt(3));
        return ApiResult.success(assistantService.listAssistantByApp(param));
    }

    /**
     * 提问
     *
	 * @author: 云裂痕
	 * @date: 2025/03/07
     */
    @PostMapping("/chat")
    public ApiResult sendMessage(@RequestBody ChatCommand command) {
        command.setApi(true);
        return ApiResult.success(llmService.chat(command));
    }

}
