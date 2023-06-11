package com.stars.interfaces.model.entity;

import lombok.Data;

/**
 * 头像参数
 * 用于随机头像接口的请求参数
 * 头像参数包含一个字段 {@code form}，用于指定请求的头像类型或来源。
 *
 * @author stars
 */
@Data
public class AvatarParams {

    /**
     * 请求的头像类型或来源
     */
    private String form;
}
