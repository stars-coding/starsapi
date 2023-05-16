package com.stars.backend.exception;

import com.stars.backend.common.BaseResponse;
import com.stars.backend.common.ErrorCode;
import com.stars.backend.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * 用于捕获并处理应用程序中的异常情况，返回适当的错误响应。
 *
 * @author stars
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     *
     * @param e 捕获到的业务异常对象
     * @return 包含错误信息的响应对象
     */
    @ExceptionHandler(BusinessException.class)
    public BaseResponse<?> businessExceptionHandler(BusinessException e) {
        this.log.error("业务异常: " + e.getMessage(), e);
        return ResultUtils.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理运行时异常
     *
     * @param e 捕获到的运行时异常对象
     * @return 包含系统错误信息的响应对象
     */
    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<?> runtimeExceptionHandler(RuntimeException e) {
        this.log.error("运行时异常", e);
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR, e.getMessage());
    }
}
