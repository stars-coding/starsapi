package com.stars.common.model.vo;

import lombok.Data;

import java.util.Date;

/**
 * 接口视图
 *
 * @author stars
 */
@Data
public class InterfVO {

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
     * 接口请求方法
     */
    private String interfRequestMethod;

    /**
     * 接口状态(0-关闭，1-开启)
     */
    private Integer interfStatus;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 用户调用接口-总计调用次数
     */
    private Long totalInvokeNum;

    /**
     * 用户调用接口-剩余调用次数
     */
    private Long leftInvokeNum;
}
