package com.stars.backend.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户添加请求
 *
 * @author stars
 */
@Data
public class UserAddRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户名称
     */
    private String userName;
    /**
     * 用户头像
     */
    private String userAvatar;
    /**
     * 用户性别(0-女，1-男)
     */
    private Integer userGender;
    /**
     * 用户角色：user/admin
     */
    private String userRole;
    /**
     * 用户账号
     */
    private String userAccount;
    /**
     * 用户密码(加密)
     */
    private String userPassword;
}
