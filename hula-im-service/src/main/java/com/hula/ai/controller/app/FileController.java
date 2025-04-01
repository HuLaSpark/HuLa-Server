package com.hula.ai.controller.app;

import com.alibaba.fastjson.JSONArray;
import com.hula.ai.client.model.command.DeleteFileParam;
import com.hula.ai.client.model.command.FileParam;
import com.hula.ai.client.model.command.UploadParam;
import com.hula.ai.framework.util.file.FileUploadResponse;
import com.hula.ai.llm.base.service.LLMService;
import com.hula.domain.vo.res.ApiResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件管理
 *
 * @author: 云裂痕
 * @date: 2025/03/13
 * @version: 1.0.0
 * 得其道 乾乾
 */
@RestController
@RequestMapping("/file")
public class FileController {
    @Autowired
    private LLMService llmService;

	/**
	 * 上传文件
	 *
	 * @author: 乾乾
	 */
	@PostMapping("/upload")
	public ApiResult<FileUploadResponse> uploadFile(@Validated @ModelAttribute UploadParam param, @RequestParam("file") MultipartFile file) {
		return ApiResult.success(llmService.uploadFile(file, param));
	}

	/**
	 * 文件列表
	 *
	 * @author: 乾乾
	 */
	@GetMapping("/fileList")
	public ApiResult<JSONArray> fileList(@Validated FileParam param) {
		return ApiResult.success(llmService.fileList(param.getModel()));
	}

	/**
	 * 删除文件
	 *
	 * @author: 乾乾
	 */
	@PostMapping("/delete")
	public ApiResult<Boolean> fileList(@Validated @RequestBody DeleteFileParam param) {
		return ApiResult.success(llmService.deleteFile(param));
	}


}
