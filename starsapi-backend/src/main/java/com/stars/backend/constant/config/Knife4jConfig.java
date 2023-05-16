package com.stars.backend.constant.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Knife4j接口文档配置
 * 用于配置Knife4j（Swagger）接口文档生成器，仅在开发环境下生效。
 * Knife4j 是一个用于生成美观的API接口文档的工具。
 *
 * @author stars
 */
@Configuration
@EnableSwagger2
@Profile("dev")
public class Knife4jConfig {

    /**
     * 创建Knife4jDocket对象，用于生成接口文档
     *
     * @return 创建的Docket对象
     */
    @Bean
    public Docket defaultApi2() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(new ApiInfoBuilder()
                        .title("Stars API")
                        .description("一个开放式的 API 在线调用平台")
                        .version("1.0.0")
                        .build())
                .select()
                // 指定Controller扫描包路径
                .apis(RequestHandlerSelectors.basePackage("com.stars.backend.controller"))
                .paths(PathSelectors.any())
                .build();
    }
}
