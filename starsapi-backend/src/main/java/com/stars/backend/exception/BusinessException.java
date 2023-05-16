package com.stars.backend.exception;

import com.stars.backend.common.ErrorCode;

/**
 * 自定义业务异常类
 * 用于表示应用程序中的业务异常情况，通常携带错误代码和错误信息。
 *
 * @author stars
 */
public class BusinessException extends RuntimeException {

    /**
     * 异常对应的错误代码
     */
    private final int code;

    /**
     * 构造一个业务异常
     *
     * @param code    错误代码
     * @param message 错误信息
     */
    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 构造一个业务异常
     *
     * @param errorCode 包含错误代码和错误信息的 ErrorCode 对象
     */
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    /**
     * 构造一个业务异常
     *
     * @param errorCode 包含错误代码和错误信息的 ErrorCode 对象
     * @param message   错误信息(可覆盖 ErrorCode 中的信息)
     */
    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }

    /**
     * 获取异常的错误代码
     *
     * @return 错误代码
     */
    public int getCode() {
        return code;
    }
}
