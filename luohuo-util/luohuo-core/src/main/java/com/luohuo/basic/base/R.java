package com.luohuo.basic.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import com.luohuo.basic.exception.BizException;
import com.luohuo.basic.exception.code.BaseExceptionCode;
import com.luohuo.basic.jackson.JsonUtil;

/**
 * @author 乾乾
 * @date 2017-12-13 10:55
 */
@Getter
@Setter
@Accessors(chain = true)
@SuppressWarnings("ALL")
public class R<T> {
	@Schema(description ="成功标识。true/false")
	private Boolean success;
    public static final String DEF_ERROR_MESSAGE = "系统繁忙，请稍候再试";
    public static final String HYSTRIX_ERROR_MESSAGE = "请求超时，请稍候再试";
    public static final int SUCCESS_CODE = 200;
    public static final int FAIL_CODE = -1;
    public static final int TIMEOUT_CODE = -2;
    /**
     * 统一参数验证异常
     */
    public static final int VALID_EX_CODE = -9;
    public static final int OPERATION_EX_CODE = -10;
    /**
     * 调用是否成功标识，0：成功，-1:系统繁忙，此时请开发者稍候再试 详情见[ExceptionCode]
     */
    @Schema(description = "响应编码:0/200-请求处理成功")
    private int code;

    /**
     * 是否执行默认操作
     */
    @JsonIgnore
    private Boolean defExec = true;

    /**
     * 调用结果
     */
    @Schema(description = "响应数据")
    private T data;

    /**
     * 结果消息，如果调用成功，消息通常为空T
     */
    @Schema(description = "提示消息")
    private String msg = "";

    @Schema(description = "请求路径")
    private String path;

	@Schema(description ="版本号")
	private String version;

	private static String baseVersion;

    /**
     * 响应时间
     */
    @Schema(description = "响应时间戳")
    private long timestamp = System.currentTimeMillis();

    private R() {
        this.defExec = false;
		this.version = baseVersion;
        this.timestamp = System.currentTimeMillis();
    }

    public R(int code, T data, String msg) {
        this.code = code;
        this.data = data;
        this.msg = msg;
        this.defExec = false;
		this.version = baseVersion;
		this.success = code ==  SUCCESS_CODE;
        this.timestamp = System.currentTimeMillis();
    }

	public static void setBaseVersion(String version) {
		R.baseVersion = version;
	}

    public static <E> R<E> result(int code, E data, String msg) {
        return new R<>(code, data, msg);
    }

    /**
     * 请求成功消息
     *
     * @param data 结果
     * @return RPC调用结果
     */
    public static <E> R<E> success(E data) {
        return new R<>(SUCCESS_CODE, data, "");
    }

	public static <T> R<T> success() {
		R<T> result = new R<>();
		result.setData(null);
		result.setCode(SUCCESS_CODE);
		result.setSuccess(Boolean.TRUE);
		return result;
	}

	public static R returnResult(Boolean b){
		return b? R.success(): R.fail();
	}

	public static R returnResult(int b){
		return b > 0? R.success(): R.fail();
	}

	public static R returnResult(String msg, int num){
		return num > 0? R.success().msg(msg + "成功"): R.fail(msg + "失败");
	}

	public static <V> R returnResult(String msg, V result){
		return result != null ? R.success().msg(msg + "成功"): R.fail(msg + "失败");
	}

	public static R returnResult(String msg, Boolean flag){
		return flag? R.success().msg(msg + "成功"): R.fail(msg + "失败");
	}

    public static <E> R<E> successDef() {
        return new R<>(SUCCESS_CODE, null, "");
    }

	public R msg(String message){
		this.setMsg(message);
		return this;
	}

    /**
     * 请求失败消息
     *
     * @param msg
     * @return
     */
    public static <E> R<E> fail(int code, String msg) {
        return new R<>(code, null, (msg == null || msg.isEmpty()) ? DEF_ERROR_MESSAGE : msg);
    }

    public static <E> R<E> fail(int code, String msg, String errorMsg) {
        return new R<>(code, null, (msg == null || msg.isEmpty()) ? DEF_ERROR_MESSAGE : msg);
    }

	public static <E> R<E> fail() {
		return fail(OPERATION_EX_CODE, "");
	}

    public static <E> R<E> fail(String msg) {
        return fail(OPERATION_EX_CODE, msg);
    }

    public static <E> R<E> fail(String msg, Object... args) {
        String message = (msg == null || msg.isEmpty()) ? DEF_ERROR_MESSAGE : msg;
        return new R<>(OPERATION_EX_CODE, null, String.format(message, args));
    }

    public static <E> R<E> fail(BaseExceptionCode exceptionCode) {
        return validFail(exceptionCode);
    }

    public static <E> R<E> fail(BizException exception) {
        if (exception == null) {
            return fail(DEF_ERROR_MESSAGE);
        }
        return new R<>(exception.getCode(), null, exception.getMessage());
    }

    /**
     * 请求失败消息，根据异常类型，获取不同的提供消息
     *
     * @param throwable 异常
     * @return RPC调用结果
     */
    public static <E> R<E> fail(Throwable throwable) {
        String msg = throwable != null ? throwable.getMessage() : DEF_ERROR_MESSAGE;
        return fail(FAIL_CODE, msg, msg);
    }

    public static <E> R<E> validFail(String msg) {
        return new R<>(VALID_EX_CODE, null, (msg == null || msg.isEmpty()) ? DEF_ERROR_MESSAGE : msg);
    }

    public static <E> R<E> validFail(String msg, Object... args) {
        String message = (msg == null || msg.isEmpty()) ? DEF_ERROR_MESSAGE : msg;
        return new R<>(VALID_EX_CODE, null, String.format(message, args));
    }

    public static <E> R<E> validFail(BaseExceptionCode exceptionCode) {
        return new R<>(exceptionCode.getCode(), null,
                (exceptionCode.getMsg() == null || exceptionCode.getMsg().isEmpty()) ? DEF_ERROR_MESSAGE : exceptionCode.getMsg());
    }

    public static <E> R<E> timeout() {
        return fail(TIMEOUT_CODE, HYSTRIX_ERROR_MESSAGE);
    }

    /**
     * 逻辑处理是否成功
     *
     * @return 是否成功
     */
    public Boolean getsuccess() {
        return this.code == SUCCESS_CODE || this.code == 200;
    }

    @Override
    public String toString() {
        return JsonUtil.toJson(this);
    }
}
