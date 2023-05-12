package com.stars.common.service;

import com.stars.common.model.entity.User;

/**
 * 内部用户服务接口
 * 该服务提供了获取已分配公钥的用户的功能。
 *
 * @author stars
 */
public interface InnerUserService {

    /**
     * 获取调用用户
     * 从数据库中查询已分配公钥的用户。
     *
     * @param accessKey 公钥
     * @return 如果查询成功，返回User对象，否则返回null
     */
    User getInvokeUser(String accessKey);
}
