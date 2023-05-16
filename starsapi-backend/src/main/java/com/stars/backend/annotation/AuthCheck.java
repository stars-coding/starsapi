package com.stars.backend.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限校验
 *
 * @author stars
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthCheck {

    /**
     * 任意角色
     *
     * @return 角色名称的数组
     */
    String[] anyRole() default {};

    /**
     * 必要角色
     *
     * @return 角色名称
     */
    String mustRole() default "";
}
