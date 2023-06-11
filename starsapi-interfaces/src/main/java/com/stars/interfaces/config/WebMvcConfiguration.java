package com.stars.interfaces.config;

import com.stars.interfaces.interceptor.WebInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 网站MVC配置
 * 配置SpringMVC框架的行为。
 *
 * @author stars
 */

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    /**
     * 添加拦截器
     * 配置拦截器的行为。
     *
     * @param registry InterceptorRegistry对象，用于注册拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 将WebInterceptor拦截器注册到拦截列表中，并拦截所有请求路径上的请求
        registry.addInterceptor(new WebInterceptor()).addPathPatterns("/**");
    }
}
