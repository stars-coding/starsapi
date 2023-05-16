package com.stars.backend.constant.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatisPlus配置
 * 用于配置MyBatisPlus持久化框架的相关属性，包括分页插件等。
 * MyBatisPlus是一个增强版的MyBatis，提供了更多的便捷功能和性能优化。
 *
 * @author stars
 */
@Configuration
@MapperScan("com.stars.backend.mapper")
public class MyBatisPlusConfig {

    /**
     * MyBatisPlus拦截器
     * 配置MyBatisPlus拦截器。
     *
     * @return 创建的MyBatisPlus拦截器对象
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        // 创建MyBatisPlus拦截器
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 向拦截器中添加一个分页插件(PaginationInnerInterceptor)。
        // 这个插件用于支持MyBatisPlus的分页查询功能，并根据数据库类型(这里指定为MySQL)进行相应的分页语句生成。
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
