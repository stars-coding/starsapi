package com.stars.interfaces.model.entity;

import lombok.Data;

/**
 * 青云客机器人参数
 * 该类用于与青云客机器人接口进行交互，并包含一个字段 {@code content}，用于传递需要发送给青云客机器人的内容。
 *
 * @author stars
 */
@Data
public class QYKParams {

    /**
     * 需要发送给青云客机器人的内容
     */
    private String content;
}
