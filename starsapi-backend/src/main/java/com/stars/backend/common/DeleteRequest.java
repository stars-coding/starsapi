package com.stars.backend.common;

import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class DeleteRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 删除对象的唯一标识符
     */
    private Long id;
}
