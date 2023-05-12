package com.stars.common.model.vo;

import lombok.Data;

import java.util.Date;

/**
 * 订单视图
 *
 * @author stars
 */
@Data
public class OrdersVO {

    /**
     * 接口主键
     */
    private Long id;

    /**
     * 接口名称
     */
    private String interfName;

    /**
     * 接口描述
     */
    private String interfDescription;

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
}
