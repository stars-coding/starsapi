package com.stars.interfaces.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 网站拦截器
 * 该类用于拦截请求并进行流量染色和请求头验证。
 *
 * @author stars
 */
public class WebInterceptor implements HandlerInterceptor {

    /**
     * 在请求处理之前进行拦截处理
     *
     * @param request  请求对象，用于获取请求信息
     * @param response 响应对象，用于设置响应信息
     * @param handler  处理程序对象
     * @return 布尔值，表示请求是否通过拦截器
     * @throws Exception 异常，标识拦截处理过程中的任何错误
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 取出头部对应的信息
        String info = request.getHeader("Info");
        // 校验流量染色
        if ("StarsFlowStaining".equals(info)) {
            return true;
        }
        return false;
    }
}
