package com.stars.backend.constant;

/**
 * 用户常量
 * 包含了与用户相关的常量定义。
 *
 * @author stars
 */
public interface UserConstant {

    /**
     * 用户登录状态的键名
     */
    String USER_LOGIN_STATE = "userLoginState";

    /**
     * 系统用户id(虚拟用户)
     */
    long SYSTEM_USER_ID = 0;

    /**
     * 默认用户权限
     */
    String DEFAULT_ROLE = "user";

    /**
     * 管理员用户权限
     */
    String ADMIN_ROLE = "admin";
}
