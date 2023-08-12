package com.stars.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stars.backend.common.ErrorCode;
import com.stars.backend.common.SimpleRedisLock;
import com.stars.backend.exception.BusinessException;
import com.stars.backend.mapper.UserInvokeInterfMapper;
import com.stars.backend.service.*;
import com.stars.common.model.entity.UserInvokeInterf;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import static com.stars.backend.constant.RedisConstants.LOCK_INTERF_KEY;

/**
 * 用户调用接口服务实现
 * 提供用户调用接口相关操作的接口定义，包括增加接口调用计数、验证用户调用接口信息、获取接口调用计数等。
 *
 * @author stars
 */
@Service
public class UserInvokeInterfServiceImpl extends ServiceImpl<UserInvokeInterfMapper, UserInvokeInterf>
        implements UserInvokeInterfService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private UserService userService;

    @Resource
    private OrdersService ordersService;

    @Resource
    private CardService cardService;

    @Resource
    private CardPayResultService cardPayResultService;

    /**
     * 增加接口调用计数
     *
     * @param interfId 接口ID
     * @param userId   用户ID
     * @return 是否成功增加接口调用计数
     */
    @Override
    public boolean addInvokeCount(long interfId, long userId) {
        if (userId <= 0L || interfId <= 0L) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String name = LOCK_INTERF_KEY + interfId;
        SimpleRedisLock lock = new SimpleRedisLock(this.stringRedisTemplate, name);
        boolean b = lock.tryLock(1200L);
        if (b) {
            UserInvokeInterf userInvokeInterf = new UserInvokeInterf();
            userInvokeInterf.setUserId(userId);
            userInvokeInterf.setInterfId(interfId);
            userInvokeInterf.setTotalInvokeNum(0L);
            userInvokeInterf.setLeftInvokeNum(99999999L);
            boolean save = save(userInvokeInterf);
            if (!save) {
                lock.unlock();
                return false;
            }
            lock.unlock();
            return true;
        }
        return false;
    }

    /**
     * 验证用户调用接口信息
     *
     * @param userInvokeInterf 用户接口信息对象
     * @param add              是否增加接口调用计数
     */
    @Override
    public void validUserInvokeInterf(UserInvokeInterf userInvokeInterf, boolean add) {
        if (userInvokeInterf == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (add && (userInvokeInterf.getUserId() <= 0 || userInvokeInterf.getInterfId() <= 0)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户或接口不存在");
        }
        if (userInvokeInterf.getLeftInvokeNum() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口剩余次数不大于1");
        }
    }

    /**
     * 获取接口调用计数
     *
     * @param userId   用户ID
     * @param interfId 接口ID
     * @return 用户对指定接口的调用计数是否已达上限
     */
    @Override
    public boolean invokeCount(long userId, long interfId) {
        // 如果用户ID小于等于零，则抛出异常
        if (userId <= 0L) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID小于等于零");
        }
        // 如果接口ID小于等于零，则抛出异常
        if (interfId <= 0L) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口ID小于等于零");
        }
        // 锁的名称
        String name = LOCK_INTERF_KEY + interfId;
        // 创建Redis锁，控制并发访问
        SimpleRedisLock lock = new SimpleRedisLock(this.stringRedisTemplate, name);
        // 尝试获取锁
        boolean lockAcquired = lock.tryLock(1200L);
        if (lockAcquired) {
            try {
                // 构建查询条件
                QueryWrapper<UserInvokeInterf> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("userId", userId);
                queryWrapper.eq("interfId", interfId);
                // 查询结果
                long count = this.count(queryWrapper);
                // 如果【用户调用接口】不存在，则创建【用户调用接口】
                if (count == 0) {
                    UserInvokeInterf userInvokeInterf = new UserInvokeInterf();
                    userInvokeInterf.setUserId(userId);
                    userInvokeInterf.setInterfId(interfId);
                    // 剩余调用次数的初始值为50次
                    userInvokeInterf.setLeftInvokeNum(50L);
                    // 将【用户调用接口】保存至数据库中
                    boolean save = this.save(userInvokeInterf);
                    // 如果未保存成功，则回false
                    if (!save) {
                        return false;
                    }
                }
                // 已存在【用户调用接口】，构建更新条件
                UpdateWrapper<UserInvokeInterf> updateWrapper = new UpdateWrapper<>();
                updateWrapper.eq("userId", userId);
                updateWrapper.eq("interfId", interfId);
                updateWrapper.gt("leftInvokeNum", 0);
                updateWrapper.setSql("totalInvokeNum = totalInvokeNum + 1, leftInvokeNum = leftInvokeNum - 1");
                // 将更新条件更新至数据库中
                boolean update = this.update(updateWrapper);
                return update;
            } finally {
                // 必须释放锁
                lock.unlock();
            }
        }
        // 如果无法获取锁，则返回false
        return false;
    }
}
