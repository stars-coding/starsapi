package com.stars.backend.model.dto.orders;

import com.stars.backend.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 订单查询请求
 *
 * @author stars
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class OrdersQueryRequest extends PageRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单主键
     */
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
     * 支付方式(0-卡号支付，1-微信支付)
     */
    private Integer payType;
    /**
     * 订单状态(0-待支付，1-已支付，2-已完成，3-未完成)
     */
    private Integer status;
}
