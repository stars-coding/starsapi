# Stars API

> 作者：Stars

一个开放式的 API 在线调用平台。


## 项目概述

&emsp;&emsp;基于SpringBoot + Dubbo + Gateway的API在线调用平台。
管理员可以接入、发布接口以及可视化接口的调用情况。
用户可以浏览、在线调试接口以及购买接口的调用次数，并且还可以通过客户端SDK轻松调用接口。


## 项目背景

&emsp;&emsp;平台的初衷是服务更广泛的体验用户和开发人员，为他们提供便捷的信息和功能获取途径。
通过 API 在线调用，协助开发者快速接入各种常用服务，例如随机头像生成、百度热搜数据、聊天机器人等。
我们的目标是提高开发效率，助力开发者轻松实现多种功能，同时丰富用户的应用体验，提升用户满意度和开发者生产力。


## 项目展示

- 平台主页

![平台主页](https://github.com/stars-coding/starsapi/blob/master/image/平台主页.png)

- 接口详情

![接口详情](https://github.com/stars-coding/starsapi/blob/master/image/接口详情.png)

- 我的接口

![我的接口](https://github.com/stars-coding/starsapi/blob/master/image/我的接口.png)

- 接口充值

![接口充值](https://github.com/stars-coding/starsapi/blob/master/image/接口充值.png)

- 我的订单

![我的订单](https://github.com/stars-coding/starsapi/blob/master/image/我的订单.png)

- 个人中心

![个人中心](https://github.com/stars-coding/starsapi/blob/master/image/个人中心.png)

- 接口管理

![接口管理](https://github.com/stars-coding/starsapi/blob/master/image/接口管理.png)

- 接口分析

![接口分析](https://github.com/stars-coding/starsapi/blob/master/image/接口分析.png)

- 系统架构

![系统架构](https://github.com/stars-coding/starsapi/blob/master/image/系统架构.png)

## 技术堆栈

前端技术栈
- 开发框架：React、Umi
- 脚手架：AntDesignPro
- 组件库：AntDesign、AntDesignComponents
- 语法扩展：TypeScript、Less
- 协调工具：OpenAPI
- 打包工具：Webpack
- 代码规范：ESLint、StyleLint、Prettier

后端技术栈
- 主语言：Java
- 框架：SpringBoot、MybatisPlus、SpringCloud
- 数据库：MySQL
- 中间件：Redis、RabbitMQ
- 注册中心：Nacos
- 服务调用：Dubbo
- 微服务网关：SpringCloudGateway
- 接口文档：Swagger、Knife4j
- 工具类库：Hutool、ApacheCommonUtils、Gson


## 项目架构

- starsapi-frontend ：用户前台。
- starsapi-backend ：管理后台。
- starsapi-gateway ：API 网关。
- starsapi-interface ：模拟接口。
- starsapi-clint-sdk ：客户端 SDK。
- starsapi-common ：共享模块。


## 功能模块

管理员(管理)
- 创建接口
- 查看接口
- 发布接口
- 更新接口
- 下线接口
- 删除接口
- 调用分析
- 发布卡密

体验者(用户)
- 浏览接口
- 查看接口
- 接口充值
- 调用接口
- 订单汇总
- 删除订单
- 操作密钥
- 分享接口
- 下载SDK
- 复制依赖

开发者(用户)
- 接口调用
