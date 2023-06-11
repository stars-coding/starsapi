package com.stars.interfaces.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 错误响应
 *
 * @author stars
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {

    /**
     * 返回的结果数据
     */
    private String resultData;
}
