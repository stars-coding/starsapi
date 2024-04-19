package com.stars.common.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 卡号表
 *
 * @author stars
 * @TableName card
 */
@TableName(value = "card")
@Data
public class Card implements Serializable {

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * 卡号主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 卡号号码
     */
    private String cardNumber;
    /**
     * 卡号密码
     */
    private String cardPassword;
    /**
     * 卡号状态(0-未使用，1-已使用)
     */
    private Integer cardStatus;
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
}
