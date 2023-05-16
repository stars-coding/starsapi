package com.stars.backend.common;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * ID请求
 * 用于表示包含一个唯一标识符的请求对象。
 *
 * @author stars
 */
@Data
@NoArgsConstructor
public class IdRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 唯一标识符
     */
    private Long id;

    /**
     * 构造函数
     * 创建一个新的IdRequest对象，并设置唯一标识符。
     *
     * @param id 唯一标识符
     */
    public IdRequest(Long id) {
        this.id = id;
    }
}
