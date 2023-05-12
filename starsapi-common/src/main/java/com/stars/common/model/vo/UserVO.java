package com.stars.common.model.vo;

import lombok.Data;

import java.util.Date;

/**
 * 用户视图
 *
 * @author stars
 */
@Data
public class UserVO {

    /**
     * 用户主键
     */
    private Long id;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户角色：user/admin
     */
    private String userRole;

    /**
     * 用户账号
     */
    private String userAccount;

    /**
     * 用户公钥
     */
    private String accessKey;

    /**
     * 用户秘钥
     */
    private String secretKey;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 用户简介
     * 非数据库字段
     */
    private String userProfile;
}
