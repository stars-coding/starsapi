package com.stars.backend.model.dto.interf;

import lombok.Data;

import java.io.Serializable;

/**
 * 接口更新请求
 *
 * @author stars
 */
@Data
public class InterfUpdateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

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
     * 接口地址
     */
    private String interfUrl;
    /**
     * 接口请求方法
     */
    private String interfRequestMethod;
    /**
     * 接口请求参数
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
}
