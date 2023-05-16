package com.stars.backend.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页帮助
 * 用于封装分页结果的帮助类，包含总记录数、总页数和当前页的数据列表。
 *
 * @param <T> 数据类型
 * @author stars
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageHelper<T> {

    /**
     * 总记录数
     */
    private int count;

    /**
     * 总页数
     */
    private int pageCount;

    /**
     * 当前页的数据列表
     */
    private List<T> list;
}
