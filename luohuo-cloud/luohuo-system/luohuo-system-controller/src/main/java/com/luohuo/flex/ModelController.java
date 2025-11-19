package com.luohuo.flex;

import com.luohuo.basic.base.R;
import com.luohuo.flex.res.ModelVO;
import com.luohuo.flex.service.ModelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 模型模块
 *
 * @author 乾乾
 * @date 2025年10月20日12:20:36
 */
@Slf4j
@RestController
@RequestMapping("/model")
@AllArgsConstructor
@Tag(name = "模型服务")
public class ModelController {

	@Resource
	private ModelService modelService;

	@GetMapping("/list")
	@Operation(summary = "获取系统全局配置")
	public R<List<ModelVO>> list() {
		return R.success(modelService.getAllModels());
	}

}
