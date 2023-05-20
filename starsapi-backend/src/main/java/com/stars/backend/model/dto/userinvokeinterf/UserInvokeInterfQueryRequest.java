package com.stars.backend.model.dto.userinvokeinterf;

import com.stars.backend.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 用户调用接口查询请求
 *
 * @author stars
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserInvokeInterfQueryRequest extends PageRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户调用接口-主键
     */
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
}
