package com.luohuo.flex;

import com.luohuo.basic.base.R;
import com.luohuo.flex.dto.TranslateRequest;
import com.luohuo.flex.dto.TranslateResponse;
import com.luohuo.flex.dto.TranslateSegmentsResponse;
import com.luohuo.flex.service.TranslateService;
import com.luohuo.flex.service.SysConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/anyTenant/translate")
@RequiredArgsConstructor
@Tag(name = "翻译服务")
public class TranslateController {

    private final TranslateService translateService;
    private final SysConfigService sysConfigService;

    @PostMapping("/text")
    @Operation(summary = "普通翻译（一次性返回）")
    public R<TranslateResponse> translate(@RequestBody TranslateRequest req) {
        String source = req.getSource() == null ? "auto" : req.getSource();
        String provider = resolveProvider(req.getProvider());
        String result = translateService.translate(req.getText(), provider, source, req.getTarget());
        String usedProvider = provider;
        return R.success(new TranslateResponse(result, usedProvider));
    }

    @PostMapping("/segment")
    @Operation(summary = "段式翻译")
    public R<TranslateSegmentsResponse> translateSegments(@RequestBody TranslateRequest req) {
        String source = req.getSource() == null ? "auto" : req.getSource();
        String provider = resolveProvider(req.getProvider());
        List<String> segments = translateService.translateSegments(req.getText(), provider, source, req.getTarget());
        String usedProvider = provider;
        return R.success(new TranslateSegmentsResponse(segments, usedProvider));
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "流式翻译")
    public SseEmitter translateStream(@RequestParam String text,
                                      @RequestParam(required = false) String provider,
                                      @RequestParam(required = false, defaultValue = "auto") String source,
                                      @RequestParam(required = false) String target) {
        SseEmitter emitter = new SseEmitter(0L);
        try {
            String resolved = resolveProvider(provider);
            List<String> segments = translateService.translateSegments(text, resolved, source, target);
            for (String seg : segments) {
                emitter.send(SseEmitter.event().name("segment").data(seg));
                try { Thread.sleep(Duration.ofMillis(120).toMillis()); } catch (InterruptedException ignored) {}
            }
            emitter.send(SseEmitter.event().name("end").data("done"));
            emitter.complete();
        } catch (IOException e) {
            emitter.completeWithError(e);
        } catch (Exception ex) {
            try {
                emitter.send(SseEmitter.event().name("error").data(ex.getMessage()));
            } catch (IOException ignored) {}
            emitter.completeWithError(ex);
        }
        return emitter;
    }

    private String resolveProvider(String provider) {
        if (provider != null && !provider.isBlank()) {
            return provider.toLowerCase();
        }
        String conf = sysConfigService.get("translateDefault");
        return (conf == null || conf.isBlank()) ? "tencent" : conf.toLowerCase();
    }
}
