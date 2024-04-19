package com.stars.backend.model.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 接口状态枚举
 * 订单状态(0-待支付，1-已支付，2-已完成，3-未完成)
 *
 * @author stars
 */
public enum OrdersStatusEnum {

    /**
     * 待支付状态
     */
    WAIT("待支付", 0),

    /**
     * 已支付状态
     */
    PAID("已支付", 1),

    /**
     * 已完成状态
     */
    SUCCESS("已完成", 2),

    /**
     * 未完成状态
     */
    FAIL("未完成", 3);

    private final String text;
    private final int value;

    /**
     * 枚举构造函数
     *
     * @param text  状态描述
     * @param value 状态值
     */
    OrdersStatusEnum(String text, int value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 获取所有状态值
     *
     * @return 所有状态值的列表
     */
    public static List<Integer> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * 获取状态值
     *
     * @return 状态值
     */
    public int getValue() {
        return value;
    }

    /**
     * 获取状态描述
     *
     * @return 状态描述
     */
    public String getText() {
        return text;
    }
}
