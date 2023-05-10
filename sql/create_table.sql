# 数据库初始化
# @author stars

-- StarsAPI基础数据库
create database if not exists starsapi
    character set = utf8mb4
    collate = utf8mb4_0900_ai_ci;


-- 切换库
use starsapi;


-- 用户表
create table if not exists user
(
    id           bigint auto_increment comment '用户主键' primary key,
    userName     varchar(255)                           null comment '用户名称',
    userAvatar   varchar(1023)                          null comment '用户头像',
    userGender   int                                    null comment '用户性别(0-女，1-男)',
    userRole     varchar(255) default 'user'            not null comment '用户角色：user/admin',
    userAccount  varchar(255)                           not null comment '用户账号',
    userPassword varchar(511)                           not null comment '用户密码(加密)',
    accessKey    varchar(511)                           not null comment '用户公钥',
    secretKey    varchar(511)                           not null comment '用户秘钥',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                 not null comment '是否删除(0-未删, 1-已删)',
    constraint uni_userAccount
        unique (userAccount)
) comment '用户表' collate = utf8mb4_0900_ai_ci;

-- 插入数据 用户表
insert into user (id, userName, userAvatar, userGender, userRole, userAccount, userPassword, accessKey, secretKey)
values (1, 'stars', 'http://stars.oss-cn-guangzhou.aliyuncs.com/eiRiPAKI-stars-avatar.jpg', 0, 'admin',
        'stars',
        'b31c99242a7ab5f1a0fc6bfcaaf27166', 'ak-stars', 'sk-stars'),
       (2, 'jack', null, 0, 'user', 'jack',
        'b31c99242a7ab5f1a0fc6bfcaaf27166', 'ak-jack', 'sk-jack');


-- 接口表
create table if not exists interf
(
    id                   bigint auto_increment comment '接口主键' primary key,
    interfName           varchar(255)                       not null comment '接口名称',
    interfDescription    varchar(255)                       null comment '接口描述',
    interfUrl            varchar(511)                       not null comment '接口地址',
    interfRequestMethod  varchar(255)                       not null comment '接口请求方法',
    interfRequestParams  text                               not null comment '接口请求参数',
    interfRequestHeader  text                               null comment '接口请求头',
    interfResponseHeader text                               null comment '接口响应头',
    interfStatus         int      default 0                 not null comment '接口状态(0-关闭，1-开启)',
    interfUserId         bigint                             not null comment '接口创建人',
    createTime           datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime           datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete             tinyint  default 0                 not null comment '是否删除(0-未删, 1-已删)'
) comment '接口表' collate = utf8mb4_0900_ai_ci;

-- 插入数据 接口表
insert into interf (id, interfName, interfDescription, interfUrl, interfRequestMethod, interfRequestParams,
                    interfRequestHeader, interfResponseHeader, interfStatus, interfUserId)
values (1, '测试接口', '获取用户名称', '/api/name/user', 'POST', '{\"username\":\"username\",\"type\":string}',
        '{ \"Content-Type\": \"application/json\" }', '{ \"Content-Type\": \"application/json\" }', 1, 1),
       (2, '随机头像', '获取随机头像', '/api/avatar/avatarUrl', 'POST', '{\"from\":\"from\",\"type\":\"string\"}',
        '{ \"Content-Type\": \"application/json\" }', '{ \"Content-Type\": \"application/json\" }', 1, 1),
       (3, '百度热搜', '百度热点数据', '/api/baidu/baiduInfo', 'POST', '{\"size\":\"size\",\"type\":\"int\"}',
        '{ \"Content-Type\": \"application/json\" }', '{ \"Content-Type\": \"application/json\" }', 1, 1),
       (4, '茉莉机器人', '提供问答', '/api/mly/reply', 'POST', '{\"content\":\"content\",\"type\":\"string\"}',
        '{ \"Content-Type\": \"application/json\" }', '{ \"Content-Type\": \"application/json\" }', 1, 1),
       (5, 'MD5加密', '输入字符串进行MD5加密', '/api/md5/conversion', 'POST', '{\"content\":\"content\",\"type\":\"string\"}',
        '{ \"Content-Type\": \"application/json\" }', '{ \"Content-Type\": \"application/json\" }', 1, 1),
       (6, 'IKUN语录', '成为爱坤粉必学', '/api/ikun/xiaoheizi', 'POST', '{\"content\":\"content\",\"type\":\"string\"}',
        '{ \"Content-Type\": \"application/json\" }', '{ \"Content-Type\": \"application/json\" }', 1, 1),
       (7, '青云客机器人', '智能聊天机器', '/api/starsapi/qyk', 'POST', '{\"content\":\"\"}',
        '{ \"name\":\"content\",\"type\":\"string\" }', '{ \"name\":\"content\",\"type\":\"string\" }', 1, 1);


-- 用户调用接口表
create table if not exists user_invoke_interf
(
    id             bigint auto_increment comment '用户调用接口-主键' primary key,
    userId         bigint                             not null comment '用户调用接口-用户主键',
    interfId       bigint                             not null comment '用户调用接口-接口主键',
    totalInvokeNum bigint   default 0                 not null comment '用户调用接口-总计调用次数',
    leftInvokeNum  bigint   default 0                 not null comment '用户调用接口-剩余调用次数',
    status         int      default 0                 not null comment '用户调用接口-状态(0-禁用，1-正常)',
    createTime     datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime     datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete       tinyint  default 0                 not null comment '是否删除(0-未删, 1-已删)'
) comment '用户调用接口表' collate = utf8mb4_0900_ai_ci;

-- 插入数据 用户调用接口表
insert into user_invoke_interf (id, userId, interfId, totalInvokeNum, leftInvokeNum, status)
values (1, 1, 1, 0, 2000, 0),
       (2, 1, 2, 0, 2000, 0),
       (3, 1, 3, 0, 2000, 0),
       (4, 1, 4, 0, 2000, 0),
       (5, 1, 5, 0, 2000, 0),
       (6, 1, 6, 0, 2000, 0),
       (7, 1, 7, 0, 2000, 0),
       (8, 2, 1, 0, 2000, 0),
       (9, 2, 4, 0, 2000, 0),
       (10, 2, 2, 0, 2000, 0);


-- 卡号表
create table if not exists card
(
    id           bigint auto_increment comment '卡号主键' primary key,
    cardNumber   varchar(255)                       not null comment '卡号号码',
    cardPassword varchar(511)                       not null comment '卡号密码',
    cardStatus   int      default 0                 not null comment '卡号状态(0-未使用，1-已使用)',
    createTime   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint  default 0                 not null comment '是否删除(0-未删, 1-已删)'
) comment '卡号表' collate = utf8mb4_0900_ai_ci;

-- 插入数据 卡号表
insert into card (id, cardNumber, cardPassword, cardStatus)
values (1, '827ccb0eea8a706c4c34a16891f84e7b', '827ccb0eea8a706c4c34a16891f84e7b', 0),
       (2, 'c8f25ade868e97cfa3aba8df851a2d87', '42ddcaaf383046fe9cc662f4f7522824', 0),
       (4, '876a948808366d40278b6e39a9635f23', 'cbdb39a52f2ba0770c5468a1933d33d1', 0),
       (5, '9a9e6d4f263ef33649f9df21acdece19', 'ef79cdb9f3b2801172d11f2c34b478d0', 0);


-- 订单表（用户购买接口的订单）
create table if not exists orders
(
    id            bigint auto_increment comment '订单主键' primary key,
    userId        bigint                             not null comment '用户主键',
    interfId      bigint                             not null comment '接口主键',
    rechargeTimes bigint   default 100               not null comment '充值次数',
    payType       int      default 0                 not null comment '支付方式(0-卡号支付，1-微信支付)',
    status        int      default 0                 not null comment '订单状态(0-待支付，1-已支付，2-已完成，3-未完成)',
    createTime    datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime    datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete      tinyint  default 0                 not null comment '是否删除(0-未删, 1-已删)'
) comment '订单表（用户购买接口的订单）' collate = utf8mb4_0900_ai_ci;

-- 插入数据 订单表
insert into orders (id, userId, interfId, rechargeTimes, payType, status)
values (1, 1, 7, 100, 2, 0),
       (2, 1, 2, 100, 0, 0),
       (3, 2, 5, 100, 0, 0),
       (4, 2, 3, 100, 0, 0);


-- 卡号结算表（采用卡号支付方式的结算订单）
create table if not exists card_pay_result
(
    id         bigint auto_increment comment '卡号结算主键' primary key,
    ordersId   bigint                             not null comment '订单主键',
    cardId     bigint                             not null comment '卡号主键',
    userId     bigint                             not null comment '用户主键',
    interfId   bigint                             not null comment '接口主键',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除(0-未删, 1-已删)'
) comment '卡号结算表（采用卡号支付方式的结算订单）' collate = utf8mb4_0900_ai_ci;

-- 插入数据 卡号结算表
insert into card_pay_result (id, ordersId, cardId, userId, interfId)
values (1, 1, 1, 1, 7);
