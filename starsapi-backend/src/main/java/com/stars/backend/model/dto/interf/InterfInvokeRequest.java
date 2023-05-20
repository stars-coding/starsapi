package com.stars.backend.model.dto.interf;

import lombok.Data;

import java.io.Serializable;

/**
 * 接口调用请求
 *
 * @author stars
 */
@Data
public class InterfInvokeRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户调用接口-接口主键
     */
    private Long id;
    /**
     * 用户请求参数
     * 接口请求参数
     */
    private String userRequestParams;
}
