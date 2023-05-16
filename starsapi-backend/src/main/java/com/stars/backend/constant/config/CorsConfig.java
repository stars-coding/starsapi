package com.stars.backend.constant.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 全局跨域配置(CORS)
 * 用于配置全局的跨域资源共享设置，以允许客户端跨域访问服务器资源。
 * 可以通过addCorsMappings方法来自定义CORS规则，此示例中配置了允许发送Cookie，
 * 允许所有来源的请求(使用allowedOriginPatterns方法)，允许的HTTP方法，允许的请求头，以及暴露的响应头。
 * 注意：在生产环境中，需要根据实际需求更精确地配置跨域规则，以提高安全性。
 *
 * @author stars
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 覆盖所有请求
        registry.addMapping("/**")
                // 允许发送Cookie
                .allowCredentials(true)
                // 放行的域名(必须用patterns，否则*会和allowCredentials冲突)，这里放行所有域名
                .allowedOriginPatterns("*")
                // 指定允许的请求方法
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                // 指定允许的请求头
                .allowedHeaders("*")
                .exposedHeaders("*");
    }
}
