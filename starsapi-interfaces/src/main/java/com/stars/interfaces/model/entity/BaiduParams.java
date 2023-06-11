package com.stars.interfaces.model.entity;

import lombok.Data;

/**
 * 百度参数
 * 用于访问百度热搜接口的请求参数
 * 百度参数包含一个字段 {@code size}，用于指定热搜榜单的大小。
 *
 * @author stars
 */
@Data
public class BaiduParams {

    /**
     * 热搜榜单的大小
     */
    private int size;
}
