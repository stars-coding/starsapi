package com.stars.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stars.common.model.entity.UserInvokeInterf;

/**
 * 用户调用接口服务接口
 * 提供用户调用接口相关操作的接口定义，包括增加接口调用计数、验证用户调用接口信息、获取接口调用计数等。
 *
 * @author stars
 */
public interface UserInvokeInterfService extends IService<UserInvokeInterf> {

    /**
     * 增加接口调用计数
     *
     * @param userId   用户ID
     * @param interfId 接口ID
     * @return 是否成功增加接口调用计数
     */
    boolean addInvokeCount(long userId, long interfId);

    /**
     * 验证用户调用接口信息
     *
     * @param userInvokeInterf 用户接口信息对象
     * @param add              是否增加接口调用计数
     */
    void validUserInvokeInterf(UserInvokeInterf userInvokeInterf, boolean add);

    /**
     * 获取接口调用计数
     *
     * @param userId   用户ID
     * @param interfId 接口ID
     * @return 用户对指定接口的调用计数是否已达上限
     */
    boolean invokeCount(long userId, long interfId);
}
