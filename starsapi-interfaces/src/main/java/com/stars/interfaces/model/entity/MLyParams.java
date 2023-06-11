package com.stars.interfaces.model.entity;

import lombok.Data;

/**
 * 茉莉机器人参数
 * 该类用于与茉莉机器人接口进行交互，并包含一个字段 {@code content}，用于传递需要发送给茉莉机器人的内容。
 *
 * @author stars
 */
@Data
public class MLyParams {

    /**
     * 茉莉机器人需要处理的内容
     */
    private String content;
}
