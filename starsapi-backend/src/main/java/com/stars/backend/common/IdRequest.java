package com.stars.backend.common;

import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class IdRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 唯一标识符
     */
    private Long id;
}
