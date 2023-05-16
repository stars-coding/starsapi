package com.stars.backend.aop;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.stars.backend.annotation.AuthCheck;
import com.stars.backend.common.ErrorCode;
import com.stars.backend.exception.BusinessException;
import com.stars.backend.service.UserService;
import com.stars.common.model.entity.User;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 权限拦截器
 *
 * @author stars
 */
@Aspect
@Component
public class AuthInterceptor {

    @Resource
    private UserService userService;

    /**
     * 执行拦截
     *
     * @param joinPoint 切点对象，包含被拦截的方法信息
     * @param authCheck 权限校验注解对象，用于指定权限校验规则
     * @return 被拦截方法的执行结果
     * @throws Throwable 可能抛出的异常
     */
    @Around("@annotation(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        // 获取任意角色列表
        List<String> anyRole = Arrays.stream(authCheck.anyRole())
                .filter(StringUtils::isNotBlank) // 过滤掉空白的角色名称
                .collect(Collectors.toList());
        // 获取必要角色
        String mustRole = authCheck.mustRole();
        // 获取请求中的属性
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        // 获取请求对象
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        // 获取当前登录用户
        User user = this.userService.getLoginUser(request);
        // 校验任意角色
        // 如果任意角色列表不为空或空白，则执行
        if (CollectionUtils.isNotEmpty(anyRole)) {
            // 如果用户角色不在任意角色列表中，表示无权限，则抛出异常
            String userRole = user.getUserRole();
            if (!anyRole.contains(userRole)) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "暂无权限");
            }
        }
        // 校验必要角色
        // 如果必要角色不为空或空白，则执行
        if (StringUtils.isNotBlank(mustRole)) {
            // 如果用户角色不等于必要角色，表示无权限，则抛出异常
            String userRole = user.getUserRole();
            if (!mustRole.equals(userRole)) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "暂无权限");
            }
        }
        // 通过权限校验，放行，继续执行被拦截的方法
        return joinPoint.proceed();
    }
}
