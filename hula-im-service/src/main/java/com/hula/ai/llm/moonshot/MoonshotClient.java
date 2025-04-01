package com.hula.ai.llm.moonshot;

import cn.hutool.http.ContentType;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonParser;
import com.hula.ai.common.constant.AuthConstant;
import com.hula.ai.framework.util.file.FileUploadResponse;
import com.hula.ai.llm.moonshot.constant.ApiConstant;
import com.hula.ai.llm.moonshot.entity.ModelsList;
import com.hula.ai.llm.moonshot.entity.request.ChatCompletion;
import com.hula.ai.llm.moonshot.entity.response.ChatResponse;
import com.hula.ai.llm.moonshot.interceptor.MoonshotInterceptor;
import com.hula.ai.llm.moonshot.interceptor.MoonshotLogger;
import com.hula.ai.llm.moonshot.listener.SSEListener;
import com.hula.domain.vo.res.ApiResult;
import com.hula.exception.BizException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 月之暗面client
 * 文档地址：https://platform.moonshot.cn/docs/api-reference#%E5%9F%BA%E6%9C%AC%E4%BF%A1%E6%81%AF
 *
 * @author: 云裂痕
 * @date: 2025/03/11
 * @version: 1.2.0
 * 得其道 乾乾
 */
@Slf4j
@NoArgsConstructor(force = true)
public class MoonshotClient {
    @NotNull
    @Getter
    @Setter
    private String apiKey;

    @Getter
    private OkHttpClient okHttpClient;

    private MoonshotClient(Builder builder) {
        apiKey = builder.apiKey;
        if (Objects.isNull(builder.okHttpClient)) {
            log.info("提示：禁止在生产环境使用BODY级别日志，可以用：NONE,BASIC,HEADERS");
            if (Objects.isNull(builder.logLevel)) {
                builder.logLevel = HttpLoggingInterceptor.Level.HEADERS;
            }
            builder.okHttpClient = this.okHttpClient(builder.logLevel);
        }
        okHttpClient = builder.okHttpClient;
    }

    /**
     * 获取模型列表
     *
     * @return
     * @throws IOException
     */
    public ApiResult<ModelsList> listModels() {
        try {
            Request request = new Request.Builder().url(ApiConstant.CHAT_LIST_MODELS_URL)
                    .addHeader(AuthConstant.JWT_TOKEN_HEADER, AuthConstant.JWT_TOKEN_PREFIX + this.apiKey).build();
            Response response = okHttpClient.newCall(request).execute();
            String body = response.body().string();
            return ApiResult.success(JSON.parseObject(body, ModelsList.class));
        } catch (IOException e) {
            e.printStackTrace();
            return ApiResult.fail(-1, "");
        }
    }

    /**
     * 对话
     *
     * @return
     * @throws IOException
     */
    public ApiResult<ChatResponse> chat(ChatCompletion chat) {
        try {
            Request request = new Request.Builder().url(ApiConstant.CHAT_COMPLETION_URL)
                    .addHeader(AuthConstant.JWT_TOKEN_HEADER, AuthConstant.JWT_TOKEN_PREFIX + this.apiKey)
                    .post(RequestBody.create(MediaType.parse(ContentType.JSON.getValue()),
                            new ObjectMapper().writeValueAsString(chat))).build();
            Response response = okHttpClient.newCall(request).execute();
            String body = response.body().string();
            return ApiResult.success(JSON.parseObject(body, ChatResponse.class));
        } catch (IOException e) {
            e.printStackTrace();
            return ApiResult.fail(-1, "");
        }
    }

    /**
     * 流式响应
     *
     */
    public Boolean streamChat(HttpServletResponse response, ChatCompletion chat, Long chatId, String parentMessageId, String version, String uid, Boolean isWs) {
        chat.stream = true;
        try {
            Request request = new Request.Builder().url(ApiConstant.CHAT_COMPLETION_URL)
                    .addHeader(AuthConstant.JWT_TOKEN_HEADER, AuthConstant.JWT_TOKEN_PREFIX + this.apiKey)
                    .post(RequestBody.create(MediaType.parse(ContentType.JSON.getValue()), new ObjectMapper().writeValueAsString(chat))).build();
            Response callResponse = okHttpClient.newCall(request).execute();
            if (callResponse.code() != 200) {
				log.error("月之暗面流式响应失败: {}", callResponse.body().string());
                return true;
            }
            SSEListener sseListener = new SSEListener(response, chatId, parentMessageId, version, uid, isWs);
            return sseListener.streamChat(callResponse);
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 获取文件列表
     *
     * @param ->
     * @return
     **/
    public JSONArray filesList() {
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + this.apiKey);
        try {
			return JSONObject.parseObject(HttpUtil.createGet(ApiConstant.UPLOAD_FILES_URL).addHeaders(headerMap).execute().body()).getJSONArray("data");
		} catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解析文件ID
     *
     * @return
     **/
	private String parseFileId(String responseBody) {
		// 这里需要根据实际的JSON结构解析
		return JsonParser.parseString(responseBody)
				.getAsJsonObject()
				.get("id")
				.getAsString();
	}

	// 获取文件内容
	private String getFileContent(String fileId) throws IOException {
		Request request = new Request.Builder()
				.url(ApiConstant.BASE_COMPLETION_URL + "/files/" + fileId + "/content")
				.header("Authorization", "Bearer " + apiKey)
				.build();

		try (Response response = okHttpClient.newCall(request).execute()) {
			return response.body().string();
		}
	}

	/**
	 * 上传文件
	 **/
	public FileUploadResponse uploadFile(MultipartFile file) {
		RequestBody requestBody;
		try {
			requestBody = new MultipartBody.Builder()
					.setType(MultipartBody.FORM)
					.addFormDataPart("purpose", "file-extract")
					.addFormDataPart("file", file.getOriginalFilename(), RequestBody.create(file.getBytes(), MediaType.parse("application/octet-stream"))).build();

			Request request = new Request.Builder()
					.url(ApiConstant.BASE_COMPLETION_URL + "/files")
					.header("Authorization", "Bearer " + apiKey)
					.post(requestBody)
					.build();

			try (Response response = okHttpClient.newCall(request).execute()) {
				if (!response.isSuccessful()) throw new IOException("文件上传错误 " + response);

				// 解析文件元数据
				String fileId = parseFileId(response.body().string());

				// 获取文件内容
				String content = getFileContent(fileId);

				JSONObject json = JSONObject.parseObject(content);

				return new FileUploadResponse(fileId, json.getString("filename"), json.getString("file_type"));
			}
		} catch (IOException e) {
			throw new BizException(e.getMessage());
		}
	}


    /**
     * 根据API 文件id查看单个文件
     *
     * @param fileId 文件id
     * @return
     **/
    public String fileInfo(String fileId) {
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + this.apiKey);
        String result = null;
        try {
            result = HttpUtil.createGet(ApiConstant.UPLOAD_FILES_URL + "/" + fileId).addHeaders(headerMap).execute().body();
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 根据文件id查看文件内容
     *
     * @param fileId 文件id
     * @return
     **/
    public String fileContent(String fileId) {
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + this.apiKey);
        String result = null;
        try {
            result = HttpUtil.createGet(ApiConstant.UPLOAD_FILES_URL + "/" + fileId + "/content").addHeaders(headerMap).execute().body();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 根据文件id删除文件
     *
     * @param fileId 文件id
     * @return
     **/
    public Boolean delfile(String fileId) {
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Bearer " + this.apiKey);
        try {
			return JSONObject.parseObject(HttpRequest.delete(ApiConstant.UPLOAD_FILES_URL + "/" + fileId).addHeaders(headerMap).execute().body()).getBoolean("deleted");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 创建默认的OkHttpClient
     */
    private OkHttpClient okHttpClient(HttpLoggingInterceptor.Level level) {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new MoonshotLogger());
        httpLoggingInterceptor.setLevel(level);
        return new OkHttpClient.Builder()
                .addInterceptor(new MoonshotInterceptor())
                .addInterceptor(httpLoggingInterceptor)
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(50, TimeUnit.SECONDS)
                .readTimeout(50, TimeUnit.SECONDS)
                .build();
    }

    /**
     * 构造
     *
     * @return
     */
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private @NotNull String apiKey;

        private OkHttpClient okHttpClient;
        private HttpLoggingInterceptor.Level logLevel;

        public Builder() {
        }

        public Builder apiKey(@NotNull String val) {
            apiKey = val;
            return this;
        }

        public Builder logLevel(HttpLoggingInterceptor.Level val) {
            logLevel = val;
            return this;
        }

        public Builder okHttpClient(OkHttpClient val) {
            okHttpClient = val;
            return this;
        }

        public MoonshotClient build() {
            return new MoonshotClient(this);
        }
    }

}
