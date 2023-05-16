package com.stars.backend.common;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 基础响应
 * 通用返回类
 *
 * @param <T> 响应数据的类型
 * @author stars
 */
@Data
@NoArgsConstructor
public class BaseResponse<T> implements Serializable {

    /**
     * 响应状态码
     */
    private int code;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 构造函数
     * 用于创建带有自定义状态码、数据和消息的响应对象。
     *
     * @param code    响应状态码
     * @param data    响应数据
     * @param message 响应消息
     */
    public BaseResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    /**
     * 构造函数
     * 用于创建带有自定义状态码和数据的响应对象，消息为空字符串。
     *
     * @param code 响应状态码
     * @param data 响应数据
     */
    public BaseResponse(int code, T data) {
        this(code, data, "");
    }

    /**
     * 构造函数
     * 用于创建基于错误代码的响应对象，数据为空，消息为错误消息。
     *
     * @param errorCode 错误代码枚举
     */
    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage());
    }
}
