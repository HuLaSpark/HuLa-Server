package com.luohuo.flex.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.luohuo.flex.service.SysConfigService;
import com.luohuo.flex.service.TranslateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TranslateServiceImpl implements TranslateService {

    private final RestTemplate restTemplate;
    private final SysConfigService sysConfigService;

    @Override
    public String translate(String text, String provider, String source, String target) {
        if (text == null || text.trim().isEmpty()) {
            return "";
        }
        String p = provider;
        if (p == null || p.isBlank()) {
            String conf = sysConfigService.get("translateDefault");
            p = (conf == null || conf.isBlank()) ? "tencent" : conf.toLowerCase();
        } else {
            p = p.toLowerCase();
        }
        return switch (p) {
            case "youdao" -> youdaoTranslate(text, source == null ? "auto" : source, target == null ? "zh-CHS" : target);
            case "tencent" -> tencentTranslate(text, source == null ? "auto" : source, target == null ? "zh" : target);
            default -> youdaoTranslate(text, source == null ? "auto" : source, target == null ? "zh-CHS" : target);
        };
    }

    @Override
    public List<String> translateSegments(String text, String provider, String source, String target) {
        String out = translate(text, provider, source, target);
        return splitSegments(out);
    }

    private List<String> splitSegments(String text) {
        List<String> result = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            return result;
        }
        String[] parts = text.split("(?<=[。！？.!?；;\n])");
        for (String p : parts) {
            String s = p.trim();
            if (!s.isEmpty()) {
                result.add(s);
            }
        }
        if (result.isEmpty()) {
            result.add(text);
        }
        return result;
    }

    private String youdaoTranslate(String text, String source, String target) {
        String appKey = sysConfigService.get("youdaoAppKey");
        String appSecret = sysConfigService.get("youdaoAppSecret");
        if (isBlank(appKey) || isBlank(appSecret)) {
            throw new IllegalStateException("有道翻译密钥未配置");
        }

        long salt = System.currentTimeMillis();
        long curtime = System.currentTimeMillis() / 1000;
        String sign = youdaoSign(text, appKey, salt, curtime, appSecret);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("q", text);
        body.add("from", source);
        body.add("to", target);
        body.add("appKey", appKey);
        body.add("salt", String.valueOf(salt));
        body.add("sign", sign);
        body.add("signType", "v3");
        body.add("curtime", String.valueOf(curtime));

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);
        String resp = restTemplate.postForObject("https://openapi.youdao.com/api", entity, String.class);
        if (resp == null) {
            throw new IllegalStateException("有道翻译响应为空");
        }
        JSONObject json = JSON.parseObject(resp);
        String errorCode = json.getString("errorCode");
        if (!"0".equals(errorCode)) {
            throw new IllegalStateException("有道翻译失败:" + errorCode);
        }
        var arr = json.getJSONArray("translation");
        if (arr == null || arr.isEmpty()) {
            throw new IllegalStateException("有道翻译结果为空");
        }
        return arr.getString(0);
    }

    private String tencentTranslate(String text, String source, String target) {
        String secretId = sysConfigService.get("tencentSecretId");
        String secretKey = sysConfigService.get("tencentApiKey");
        if (isBlank(secretId) || isBlank(secretKey)) {
            throw new IllegalStateException("腾讯云翻译密钥未配置");
        }

        long timestamp = Instant.now().getEpochSecond();
        String dateStr = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneOffset.UTC).format(Instant.ofEpochSecond(timestamp));
        String service = "tmt";

        JSONObject payload = new JSONObject();
        payload.put("SourceText", text);
        payload.put("Source", source);
        payload.put("Target", target);
        payload.put("ProjectId", 0);

        String hashedRequestPayload = sha256Hex(payload.toJSONString());
        String canonicalRequest = String.join("\n",
                "POST",
                "/",
                "",
                "content-type:application/json",
                "host:tmt.tencentcloudapi.com",
                "",
                "content-type;host",
                hashedRequestPayload
        );
        String credentialScope = dateStr + "/" + service + "/tc3_request";
        String hashedCanonicalRequest = sha256Hex(canonicalRequest);
        String stringToSign = String.join("\n",
                "TC3-HMAC-SHA256",
                String.valueOf(timestamp),
                credentialScope,
                hashedCanonicalRequest
        );

        byte[] kDate = hmacSha256(dateStr.getBytes(StandardCharsets.UTF_8), ("TC3" + secretKey).getBytes(StandardCharsets.UTF_8));
        byte[] kService = hmacSha256(service.getBytes(StandardCharsets.UTF_8), kDate);
        byte[] kSigning = hmacSha256("tc3_request".getBytes(StandardCharsets.UTF_8), kService);
        String signature = bytesToHex(hmacSha256(stringToSign.getBytes(StandardCharsets.UTF_8), kSigning));

        String authorization = "TC3-HMAC-SHA256 Credential=" + secretId + "/" + credentialScope + ", SignedHeaders=content-type;host, Signature=" + signature;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("X-TC-Action", "TextTranslate");
        headers.add("X-TC-Version", "2018-03-21");
        headers.add("X-TC-Region", "ap-guangzhou");
        headers.add("X-TC-Timestamp", String.valueOf(timestamp));
        headers.add("X-TC-SecretId", secretId);
        headers.add("Authorization", authorization);

        HttpEntity<String> entity = new HttpEntity<>(payload.toJSONString(), headers);
        String resp = restTemplate.postForObject("https://tmt.tencentcloudapi.com", entity, String.class);
        if (resp == null) {
            throw new IllegalStateException("腾讯云翻译响应为空");
        }
        JSONObject json = JSON.parseObject(resp);
        JSONObject response = json.getJSONObject("Response");
        if (response == null) {
            throw new IllegalStateException("腾讯云翻译结果为空");
        }
        if (response.containsKey("Error")) {
            String msg = response.getJSONObject("Error").getString("Message");
            throw new IllegalStateException("腾讯云翻译失败:" + msg);
        }
        String targetText = response.getString("TargetText");
        if (isBlank(targetText)) {
            throw new IllegalStateException("腾讯云翻译结果为空");
        }
        return targetText;
    }

    private String youdaoSign(String text, String appKey, long salt, long curtime, String appSecret) {
        String input = text.length() <= 20 ? text : text.substring(0, 10) + text.length() + text.substring(text.length() - 10);
        String raw = appKey + input + salt + curtime + appSecret;
        return sha256Hex(raw);
    }

    private String sha256Hex(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(s.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(digest);
        } catch (Exception e) {
            throw new IllegalStateException("SHA-256 计算失败", e);
        }
    }

    private byte[] hmacSha256(byte[] data, byte[] key) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(key, "HmacSHA256"));
            return mac.doFinal(data);
        } catch (Exception e) {
            throw new IllegalStateException("HMAC-SHA256 计算失败", e);
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            String h = Integer.toHexString(b & 0xff);
            if (h.length() == 1) sb.append('0');
            sb.append(h);
        }
        return sb.toString();
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
