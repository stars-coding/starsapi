package com.stars.common.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户表
 *
 * @author stars
 * @TableName user
 */
@TableName(value = "user")
@Data
public class User implements Serializable {

    /**
     * 用户主键
     */
    @TableId(type = IdType.AUTO)
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
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除(0-未删, 1-已删)
     */
    @TableLogic
    private Byte isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
