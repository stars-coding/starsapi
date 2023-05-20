package com.stars.backend.model.dto.userinvokeinterf;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户调用接口添加请求
 *
 * @author stars
 */
@Data
public class UserInvokeInterfAddRequest implements Serializable {

    private static final long serialVersionUID = 1L;

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
}
