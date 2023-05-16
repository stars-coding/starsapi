package com.stars.backend.common;

import com.stars.backend.constant.CommonConstant;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分页请求
 * 用于表示分页查询的请求参数，包括当前页号、页面大小、排序字段和排序顺序。
 *
 * @author stars
 */
@Data
@NoArgsConstructor
public class PageRequest {

    /**
     * 当前页号
     */
    private int current;

    /**
     * 页面大小
     */
    private int pageSize;

    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 排序顺序(默认升序)
     */
    private String sortOrder = CommonConstant.SORT_ORDER_ASC;
}
