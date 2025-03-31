package com.hula.core.chat.controller;

import com.hula.core.chat.domain.dto.ConverseMessageDto;
import com.hula.core.chat.service.ConverseService;
import com.hula.utils.RequestHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/converse")
@Tag(name = "converse", description = "通话模块")
public class VideoChatController {

    @Resource
    private ConverseService converseService;

    @PostMapping("/video")
    @Operation(summary = "视频通话", tags = {"video"}, description = "视频消息")
    @Parameters({@Parameter(name = "converseMessageDto", required = true, in = ParameterIn.DEFAULT)})
    public void video(@RequestBody ConverseMessageDto converseMessageDto) {
        converseService.video(RequestHolder.get().getUid(), converseMessageDto);
    }

	@PostMapping("/phone")
	@Operation(summary = "语音通话", tags = {"phone"}, description = "语音消息")
	@Parameters({@Parameter(name = "converseMessageDto", required = true, in = ParameterIn.DEFAULT)})
	public void phone(@RequestBody ConverseMessageDto converseMessageDto) {
		converseService.phone(RequestHolder.get().getUid(), converseMessageDto);
	}

}
