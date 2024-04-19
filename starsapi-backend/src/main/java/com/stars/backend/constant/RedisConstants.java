package com.stars.backend.constant;

/**
 * Redis常量
 * 包含用于Redis缓存和锁的键名常量。
 * 键名的命名规则是 "类型:用途"。
 *
 * @author stars
 */
public class RedisConstants {

    /**
     * 缓存接口数据的键前缀
     */
    public static final String CACHE_INTERF_KEY = "cache:interf:";

    /**
     * 锁定接口的键前缀
     */
    public static final String LOCK_INTERF_KEY = "lock:interf:";

    /**
     * 缓存我的订单数据的键前缀
     */
    public static final String CACHE_MY_ORDERS_KEY = "cache:myOrders:";

    /**
     * 缓存用户信息的键前缀
     */
    public static final String CACHE_USER_KEY = "cache:user:";

    /**
     * 锁定添加订单的键前缀
     */
    public static final String LOCK_ADD_ORDER_KEY = "lock:addOrders:";

    /**
     * 锁定支付订单的键前缀
     */
    public static final String LOCK_PAY_ORDER_KEY = "lock:payOrders:";

    /**
     * 用于缓存击穿，使用互斥锁解决的键前缀
     */
    public static final String LOCK_INTERF_BY_BREAK = "lock:interf:break:";

    /**
     * 用户信息缓存的超时时间(秒)
     */
    public static final Long USER_TIME_OUT = 30L;
}
