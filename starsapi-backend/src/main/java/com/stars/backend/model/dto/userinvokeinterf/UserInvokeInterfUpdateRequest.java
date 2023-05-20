package com.stars.backend.model.dto.userinvokeinterf;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户调用接口更新请求
 *
 * @author stars
 */
@Data
public class UserInvokeInterfUpdateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户调用接口-主键
     */
    private Long id;
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
}
