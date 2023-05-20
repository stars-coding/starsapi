package com.stars.backend.model.dto.orders;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 订单删除请求
 *
 * @author stars
 */
@Data
public class OrdersDeleteRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单主键
     */
    private Long id;
    /**
     * 订单状态(0-待支付，1-已支付，2-已完成，3-未完成)
     */
    private Integer status;
    /**
     * 创建时间
     */
    private Date createTime;
}
