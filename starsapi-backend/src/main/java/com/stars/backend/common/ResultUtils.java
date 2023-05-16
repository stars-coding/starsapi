package com.stars.backend.common;

/**
 * 结果集工具
 * 用于生成不同类型的响应结果的工具类，包括成功响应、错误响应等。
 *
 * @author stars
 */
public class ResultUtils {

    /**
     * 成功响应
     *
     * @param data 响应数据
     * @param <T>  响应数据的类型
     * @return 成功的响应对象
     */
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(0, data, "ok");
    }

    /**
     * 错误响应
     *
     * @param errorCode 错误代码枚举
     * @return 包含错误代码的响应对象
     */
    public static BaseResponse error(ErrorCode errorCode) {
        return new BaseResponse<>(errorCode);
    }

    /**
     * 错误响应
     *
     * @param code    错误状态码
     * @param message 错误消息
     * @return 包含错误状态码和消息的响应对象
     */
    public static BaseResponse error(int code, String message) {
        return new BaseResponse(code, null, message);
    }

    /**
     * 错误响应
     *
     * @param errorCode 错误代码枚举
     * @param message   错误消息
     * @return 包含错误代码和消息的响应对象
     */
    public static BaseResponse error(ErrorCode errorCode, String message) {
        return new BaseResponse(errorCode.getCode(), null, message);
    }
}
