package com.stars.backend.model.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 接口状态枚举
 * 包含接口状态的枚举值，如下线和发布，以及与其相关的操作方法。
 *
 * @author stars
 */
public enum InterfStatusEnum {

    /**
     * 下线状态
     */
    OFFLINE("下线", 0),

    /**
     * 发布状态
     */
    ONLINE("发布", 1);

    private final String text;
    private final int value;

    /**
     * 枚举构造函数
     *
     * @param text  状态描述
     * @param value 状态值
     */
    InterfStatusEnum(String text, int value) {
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
