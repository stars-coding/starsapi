package com.stars.common.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户调用接口表
 *
 * @author stars
 * @TableName user_invoke_interf
 */
@TableName(value = "user_invoke_interf")
@Data
public class UserInvokeInterf implements Serializable {

    /**
     * 用户调用接口-主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户调用接口-用户主键
     */
    private Long userId;

    /**
     * 用户调用接口-接口主键
     */
    private Long interfId;

    /**
     * 用户调用接口-总计调用次数
     */
    private Long totalInvokeNum;

    /**
     * 用户调用接口-剩余调用次数
     */
    private Long leftInvokeNum;

    /**
     * 用户调用接口-状态(0-禁用，1-正常)
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

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
