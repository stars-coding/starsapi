package com.stars.backend.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请
 *
 * @author stars
 */
@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    /**
     * 用户账号
     */
    private String userAccount;
    /**
     * 用户密码(加密)
     */
    private String userPassword;
    /**
     * 校验密码
     */
    private String checkPassword;
}
