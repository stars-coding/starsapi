package com.stars.backend.common;

import org.springframework.data.redis.core.RedisTemplate;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Redis乐观锁
 * 用于实现基于Redis的乐观锁，以确保在多线程环境中对共享资源的安全访问。
 * 使用方法：
 * 1. 创建SimpleRedisLock对象，传入StringRedisTemplate和锁的名称。
 * 2. 调用tryLock方法尝试获取锁，如果成功返回true，否则返回false。
 * 3. 在获取锁后，执行需要加锁的代码。
 * 4. 执行完成后，调用unlock方法释放锁。
 * 锁的名称由参数name决定，确保在不同的业务场景中使用不同的名称以避免冲突。
 * 使用线程ID作为锁的标识，确保每个线程只能释放自己持有的锁。
 *
 * @author stars
 */
public class SimpleRedisLock implements ILock {

    private static final String KEY_PREFIX = "lock:";

    private static final String ID_PREFIX = UUID.randomUUID().toString() + "-";

    /**
     * Redis面板
     */
    private RedisTemplate redisTemplate;

    /**
     * 锁的名称
     */
    private String name;

    /**
     * 构造函数
     * 创建Redis乐观锁。
     *
     * @param redisTemplate Redis面板
     * @param name          锁的名称
     */
    public SimpleRedisLock(RedisTemplate redisTemplate, String name) {
        this.redisTemplate = redisTemplate;
        this.name = name;
    }

    /**
     * 获取锁
     * 尝试锁定资源。
     *
     * @param timeoutSec 超时时间(毫秒)，如果在指定时间内无法获取锁，则返回false
     * @return 如果成功获取锁，返回true，否则返回false
     */
    @Override
    public boolean tryLock(long timeoutSec) {
        // 获取线程ID标识，作为锁的标识，确保每个线程只能释放自己持有的锁
        String threadId = this.ID_PREFIX + Thread.currentThread().getId();
        // 开始获取锁
        Boolean success = this.redisTemplate.opsForValue()
                .setIfAbsent(this.KEY_PREFIX + this.name, threadId, timeoutSec, TimeUnit.SECONDS);
        return success;
    }

    /**
     * 释放锁
     * 解锁资源。
     */
    @Override
    public void unlock() {
        // 获取线程ID标识，作为锁的标识，确保每个线程只能释放自己持有的锁
        String threadId = this.ID_PREFIX + Thread.currentThread().getId();
        // 获取锁的ID
        String id = (String) this.redisTemplate.opsForValue().get(this.KEY_PREFIX + this.name);
        // 如果两个ID相同，则释放锁
        if (threadId.equals(id)) {
            this.redisTemplate.delete(this.KEY_PREFIX + this.name);
        }
    }
}
