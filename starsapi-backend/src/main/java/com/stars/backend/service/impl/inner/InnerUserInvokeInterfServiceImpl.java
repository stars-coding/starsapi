package com.stars.backend.service.impl.inner;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.stars.backend.common.ErrorCode;
import com.stars.backend.exception.BusinessException;
import com.stars.backend.service.UserInvokeInterfService;
import com.stars.common.model.entity.UserInvokeInterf;
import com.stars.common.service.InnerUserInvokeInterfService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 内部用户调用接口服务实现
 * 该服务提供了统计用户调用接口的次数和判断是否具有剩余调用次数的功能。
 *
 * @author stars
 */
@DubboService
public class InnerUserInvokeInterfServiceImpl implements InnerUserInvokeInterfService {

    @Autowired
    private UserInvokeInterfService userInvokeInterfService;

    /**
     * 统计调用次数
     * 统计用户调用接口的次数。
     *
     * @param userId   用户ID
     * @param interfId 接口ID
     * @return 如果统计成功，返回true，否则返回false
     */
    @Override
    public boolean invokeCount(long userId, long interfId) {
        // 统计调用次数
        return this.userInvokeInterfService.invokeCount(userId, interfId);
    }

    /**
     * 校验剩余次数
     * 判断是否具有剩余调用次数。
     *
     * @param userId   用户ID
     * @param interfId 接口ID
     * @return 如果具有剩余调用次数，返回true，否则返回false
     */
    @Override
    public boolean validLeftNum(Long userId, Long interfId) {
        // 构建查询条件
        LambdaQueryWrapper<UserInvokeInterf> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserInvokeInterf::getUserId, userId).eq(UserInvokeInterf::getInterfId, interfId);
        // 调用查询
        UserInvokeInterf userInvokeInterf = this.userInvokeInterfService.getOne(lqw);
        // 如果【用户调用接口】为空，表示不存在，则抛出异常
        if (userInvokeInterf == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "用户调用接口不存在");
        }
        // 如果剩余调用次数小于等于零，则抛出异常
        if (userInvokeInterf.getLeftInvokeNum() <= 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "剩余调用次数小于等于零");
        }
        return true;
    }
}
