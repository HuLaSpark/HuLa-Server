package com.luohuo.flex.ai.controller.model;

import com.luohuo.flex.ai.common.pojo.PageResult;
import com.luohuo.flex.ai.controller.model.vo.apikey.AiApiKeyBalanceRespVO;
import com.luohuo.flex.ai.controller.model.vo.apikey.AiApiKeyPageReqVO;
import com.luohuo.flex.ai.controller.model.vo.apikey.AiApiKeyRespVO;
import com.luohuo.flex.ai.controller.model.vo.apikey.AiApiKeySaveReqVO;
import com.luohuo.flex.ai.controller.model.vo.model.AiModelRespVO;
import com.luohuo.flex.ai.dal.model.AiApiKeyDO;
import com.luohuo.flex.ai.service.model.AiApiKeyService;
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
import static com.luohuo.basic.utils.collection.CollectionUtils.convertList;


@Tag(name = "管理后台 - AI API 密钥")
@RestController
@RequestMapping("/api-key")
@Validated
public class AiApiKeyController {

    @Resource
    private AiApiKeyService apiKeyService;

    @PostMapping("/create")
    @Operation(summary = "创建 API 密钥")
    public R<Long> createApiKey(@Valid @RequestBody AiApiKeySaveReqVO createReqVO) {
        return success(apiKeyService.createApiKey(createReqVO, ContextUtil.getUid()));
    }

    @PutMapping("/update")
    @Operation(summary = "更新 API 密钥")
    public R<Boolean> updateApiKey(@Valid @RequestBody AiApiKeySaveReqVO updateReqVO) {
        apiKeyService.updateApiKey(updateReqVO, ContextUtil.getUid());
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除 API 密钥")
    @Parameter(name = "id", description = "编号", required = true)
    public R<Boolean> deleteApiKey(@RequestParam("id") Long id) {
        apiKeyService.deleteApiKey(id, ContextUtil.getUid());
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得 API 密钥")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public R<AiApiKeyRespVO> getApiKey(@RequestParam("id") Long id) {
        AiApiKeyDO apiKey = apiKeyService.getApiKey(id);
        return success(BeanUtils.toBean(apiKey, AiApiKeyRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得 API 密钥分页（包含系统公开密钥和用户私有密钥）")
    public R<PageResult<AiApiKeyRespVO>> getApiKeyPage(@Valid AiApiKeyPageReqVO pageReqVO) {
        PageResult<AiApiKeyDO> pageResult = apiKeyService.getApiKeyPage(pageReqVO, ContextUtil.getUid());
        return success(BeanUtils.toBean(pageResult, AiApiKeyRespVO.class));
    }

    @GetMapping("/simple-list")
    @Operation(summary = "获得 API 密钥简单列表（包含系统公开密钥和用户私有密钥）")
    public R<List<AiModelRespVO>> getApiKeySimpleList() {
        List<AiApiKeyDO> list = apiKeyService.getApiKeyList(ContextUtil.getUid());
        return success(convertList(list, key -> new AiModelRespVO().setId(key.getId()).setName(key.getName()).setPlatform(key.getPlatform())));
    }

    @GetMapping("/balance")
    @Operation(summary = "查询 API 密钥余额")
    @Parameter(name = "id", description = "API密钥编号", required = true, example = "1024")
    public R<AiApiKeyBalanceRespVO> getApiKeyBalance(@RequestParam("id") Long id) {
        return success(apiKeyService.getApiKeyBalance(id, ContextUtil.getUid()));
    }

}