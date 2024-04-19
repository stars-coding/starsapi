package com.stars.common.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 订单表（用户购买接口的订单）
 *
 * @author stars
 * @TableName orders
 */
@TableName(value = "orders")
@Data
public class Orders implements Serializable {

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * 订单主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 用户主键
     */
    private Long userId;
    /**
     * 接口主键
     */
    private Long interfId;
    /**
     * 充值次数
     */
    private Long rechargeTimes;
    /**
     * 支付方式(0-卡号支付，1-微信支付)
     */
    private Integer payType;
    /**
     * 订单状态(0-待支付，1-已支付，2-已完成，3-未完成)
     */
    private Integer status;
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
