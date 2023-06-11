package com.stars.interfaces.model.entity;

import lombok.Data;

/**
 * IKUN参数
 * 用于访问IKUN语录接口的请求参数
 * IKUN参数包含一个字段 {@code content}，用于指定需要获取的语录内容。
 *
 * @author stars
 */
@Data
public class IKunParams {

    /**
     * 语录内容
     */
    private String content;
}
