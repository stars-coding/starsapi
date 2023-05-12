package com.stars.common.service;

/**
 * 内部用户调用接口服务接口
 * 该服务提供了统计用户调用接口的次数和判断是否具有剩余调用次数的功能。
 *
 * @author stars
 */
public interface InnerUserInvokeInterfService {

    /**
     * 统计调用次数
     * 统计用户调用接口的次数。
     *
     * @param userId   用户ID
     * @param interfId 接口ID
     * @return 如果统计成功，返回true，否则返回false
     */
    boolean invokeCount(long userId, long interfId);

    /**
     * 校验剩余次数
     * 判断是否具有剩余调用次数。
     *
     * @param userId   用户ID
     * @param interfId 接口ID
     * @return 如果具有剩余调用次数，返回true，否则返回false
     */
    boolean validLeftNum(Long userId, Long interfId);
}
