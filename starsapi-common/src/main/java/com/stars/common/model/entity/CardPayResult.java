package com.stars.common.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 卡号结算表（采用卡号支付方式的结算订单）
 *
 * @author stars
 * @TableName card_pay_result
 */
@TableName(value = "card_pay_result")
@Data
public class CardPayResult implements Serializable {

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * 卡号结算主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 订单主键
     */
    private Long ordersId;
    /**
     * 卡号主键
     */
    private Long cardId;
    /**
     * 用户主键
     */
    private Long userId;
    /**
     * 接口主键
     */
    private Long interfId;
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
