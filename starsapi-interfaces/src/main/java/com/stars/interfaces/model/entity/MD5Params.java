package com.stars.interfaces.model.entity;

import lombok.Data;

/**
 * MD5参数
 * 用于访问MD5加密接口的请求参数
 * MD5参数包含一个字段 {@code content}，用于指定需要进行MD5加密的内容。
 *
 * @author stars
 */
@Data
public class MD5Params {

    /**
     * 需要进行MD5加密的内容
     */
    private String content;
}
