package com.stars.backend.aop;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

/**
 * 日志拦截器
 *
 * @author stars
 */
@Aspect
@Component
@Slf4j
public class LogInterceptor {

    /**
     * 执行拦截
     *
     * @param point 切点对象，包含被拦截的方法信息
     * @return 被拦截方法的执行结果
     * @throws Throwable 可能抛出的异常
     */
    @Around("execution(* com.stars.backend.controller.*.*(..))")
    public Object doInterceptor(ProceedingJoinPoint point) throws Throwable {
        // 计时
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        // 获取请求中的属性
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        // 获取请求对象
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        // 生成请求ID
        String requestId = UUID.randomUUID().toString();
        // 获取请求的URL
        String url = request.getRequestURI();
        // 获取请求参数
        Object[] args = point.getArgs();
        // 将参数数组转为字符串
        String reqParam = "[" + StringUtils.join(args, ", ") + "]";
        // 输出请求日志
        this.log.info("request start，id: {}, path: {}, ip: {}, params: {}", requestId, url, request.getRemoteHost(), reqParam);
        // 执行原方法
        Object result = point.proceed();
        // todo 输出响应日志
        // 停止计时
        stopWatch.stop();
        // 获取总耗时
        long totalTimeMillis = stopWatch.getTotalTimeMillis();
        // 输出请求结束日志
        this.log.info("request end, id: {}, cost: {}ms", requestId, totalTimeMillis);
        // 返回原方法的执行结果
        return result;
    }
}
