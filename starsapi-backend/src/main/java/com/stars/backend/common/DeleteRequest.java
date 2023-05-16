package com.stars.backend.common;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 删除请求
 * 用于表示删除操作的请求对象，包含要删除项的唯一标识符。
 *
 * @author stars
 */
@Data
@NoArgsConstructor
public class DeleteRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 删除对象的唯一标识符
     */
    private Long id;

    /**
     * 构造函数
     * 创建一个新的DeleteRequest对象，并设置要删除项的唯一标识符。
     *
     * @param id 要删除项的唯一标识符
     */
    public DeleteRequest(Long id) {
        this.id = id;
    }
}
