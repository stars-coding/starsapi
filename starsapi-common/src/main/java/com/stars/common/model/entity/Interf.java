package com.stars.common.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 接口表
 *
 * @author stars
 * @TableName interf
 */
@TableName(value = "interf")
@Data
public class Interf implements Serializable {

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * 接口主键
     */
    @TableId(type = IdType.AUTO)
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
     * 接口地址
     */
    private String interfUrl;
    /**
     * 接口请求方法
     */
    private String interfRequestMethod;
    /**
     * 接口请求参数
     * [
     * {"name": "username", "type": "string"}
     * ]
     */
    private String interfRequestParams;
    /**
     * 接口请求头
     */
    private String interfRequestHeader;
    /**
     * 接口响应头
     */
    private String interfResponseHeader;
    /**
     * 接口状态(0-关闭，1-开启)
     */
    private Integer interfStatus;
    /**
     * 接口创建人
     */
    private Long interfUserId;
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
