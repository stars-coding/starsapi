package com.stars.common.service;

import com.stars.common.model.entity.Interf;

/**
 * 内部接口服务接口
 * 该服务提供了查询接口是否存在于数据库中的功能。
 *
 * @author stars
 */
public interface InnerInterfService {

    /**
     * 获取接口
     * 从数据库中获取接口。
     *
     * @param interfUrl           接口URL
     * @param interfRequestMethod 接口请求方法
     * @return 如果接口存在，则返回对应的接口实体，否则返回null
     */
    Interf getInterf(String interfUrl, String interfRequestMethod);
}
