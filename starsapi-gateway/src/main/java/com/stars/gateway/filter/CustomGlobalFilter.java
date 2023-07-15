package com.stars.gateway.filter;

import com.stars.clientsdk.utils.SignUtils;
import com.stars.common.model.entity.Interf;
import com.stars.common.model.entity.User;
import com.stars.common.service.InnerInterfService;
import com.stars.common.service.InnerUserInvokeInterfService;
import com.stars.common.service.InnerUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 自定义全局过滤器
 *
 * @author stars
 */
@Slf4j
@Component
public class CustomGlobalFilter implements GlobalFilter, Ordered {

    @DubboReference
    private InnerUserService innerUserService;

    @DubboReference
    private InnerInterfService innerInterfService;

    @DubboReference
    private InnerUserInvokeInterfService innerUserInvokeInterfService;

    // IP黑名单
    private static final List<String> IP_BLACK_LIST = Arrays.asList(new String[]{"192.168.123.111"});

    // IP白名单
    private static final List<String> IP_WHITE_LIST = Arrays.asList(new String[]{""});

    // todo 开发环境-本地地址-模拟接口地址
    private static final String INTERFACE_HOST = "http://localhost:28004";
    // todo 线上环境-服务器公网地址-模拟接口地址
//    private static final String INTERFACE_HOST = "";

    /**
     * 对请求进行过滤处理
     *
     * @param exchange 路由交换机，用于访问请求和响应
     * @param chain    责任链模式，用于继续请求链的处理
     * @return 字段类型为Mono<Void>的对象，表示异步处理结果
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1、请求日志
        // 获取请求对象
        ServerHttpRequest request = exchange.getRequest();
        // 获取请求中的数据
        String id = request.getId();
        String path = this.INTERFACE_HOST + request.getPath().value();
        String method = request.getMethod().toString();
        MultiValueMap<String, String> queryParams = request.getQueryParams();
        String sourceAddress = request.getLocalAddress().getHostString();
        InetSocketAddress remoteAddress = request.getRemoteAddress();
        this.log.info("请求唯一标识" + id);
        this.log.info("请求路径" + path);
        this.log.info("请求方法" + method);
        this.log.info("请求参数" + queryParams);
        this.log.info("请求来源地址" + sourceAddress);
        this.log.info("请求来源地址" + remoteAddress);
        // 2、访问控制-黑名单
        // 获取响应对象，已经对response做了增强，装饰者模式
        ServerHttpResponse response = exchange.getResponse();
        // 如果源地址在IP黑名单中，则直接返回结束
        if (this.IP_BLACK_LIST.contains(sourceAddress)) {
            return this.handleNoAuth(response);
        }
        // 3、用户鉴权-鉴定ak和sk
        // 获取请求头
        HttpHeaders headers = request.getHeaders();
        // 获取请求头中的数据
        String accessKey = headers.getFirst("accessKey");
        String nonce = headers.getFirst("nonce");
        String timestamp = headers.getFirst("timestamp");
        String sign = headers.getFirst("sign");
        String body = headers.getFirst("body");
        // 声明调用用户（调用者）
        User invokeUser = null;
        // 根据ak从数据库中获取调用用户-RPC远程调用
        try {
            invokeUser = this.innerUserService.getInvokeUser(accessKey);
        } catch (Exception e) {
            this.log.error("getInvokeUser error", e);
        }
        // 如果未查到调用用户，则直接返回结束
        if (invokeUser == null) {
            return this.handleNoAuth(response);
        }
        // 如果随机数为空或长度大于10000，则直接返回结束
        if (nonce == null || Long.parseLong(nonce) > 10000L) {
            return this.handleNoAuth(response);
        }
        // 如果时间间隔大于五分钟，则直接返回结束
        long currentTime = System.currentTimeMillis() / 1000L;
        final long FIVE_MINUTES = 60 * 5L;
        if ((currentTime - Long.parseLong(timestamp)) >= FIVE_MINUTES) {
            return this.handleNoAuth(response);
        }
        // 获取调用用户的sk
        String secretKey = invokeUser.getSecretKey();
        // 签名认证算法生成sk
        String serverSign = SignUtils.getSign(body, secretKey);
        // 如果sk不正确，则直接返回结束
        if (sign == null || !sign.equals(serverSign)) {
            return this.handleNoAuth(response);
        }
        // 4、请求的模拟接口存在与否
        // 声明接口
        Interf interf = null;
        // 根据路径和方法查看是否存在此接口-RPC远程调用
        try {
            String requestPath = request.getPath().value();
            interf = this.innerInterfService.getInterf(requestPath, method);
        } catch (Exception e) {
            this.log.error("getInterf error", e);
        }
        // 如果请求的接口不存在，则直接返回结束
        if (interf == null) {
            return this.handleNoAuth(response);
        }
        // 5、统一业务处理-判断用户是否具有接口的调用权限和调用剩余次数
        // 声明剩余次数
        boolean isLeftNum = false;
        // 根据调用用户ID和接口ID查看是否具有剩余调用次数-RPC远程调用
        try {
            isLeftNum = this.innerUserInvokeInterfService.validLeftNum(invokeUser.getId(), interf.getId());
        } catch (Exception e) {
            this.log.error("validLeftNum", e);
        }
        // 如果没有剩余调用次数，则直接返回结束
        if (!isLeftNum) {
            return this.handleNoAuth(response);
        }
        // todo
        // 6、流量染色-对源请求的请求头添加新内容
        ServerHttpRequest modifyRequest = request.mutate().header("Info", "StarsFlowStaining").build();
        ServerWebExchange newExchange = exchange.mutate().request(modifyRequest).build();
        // 调用处理响应
        return this.handleResponse(newExchange, chain, invokeUser.getId(), interf.getId());
    }

    /**
     * 处理响应
     *
     * @param exchange 路由交换机，用于访问请求和响应
     * @param chain    责任链模式，用于继续请求链的处理
     * @param userId   用户ID
     * @param interfId 接口ID
     * @return 字段类型为Mono<Void>的对象，表示异步处理结果
     */
    public Mono<Void> handleResponse(ServerWebExchange exchange, GatewayFilterChain chain, long userId, long interfId) {
        try {
            // 获取原始的响应对象
            ServerHttpResponse originalResponse = exchange.getResponse();
            // 获取数据缓冲工厂
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();
            // 获取响应状态码
            HttpStatus statusCode = originalResponse.getStatusCode();
            // 判断响应状态码是否为200、OK（现在没有调用，感觉拿不到响应码）
            if (statusCode == HttpStatus.OK) {
                // 创建装饰后的响应对象（装饰，增强能力）
                ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
                    // 等调用完转发的接口后才会执行
                    // 重写writeWith方法，用于处理响应体的数据
                    // 这段方法就是只要当我们的模拟接口调用完成之后,等它返回结果，
                    // 就会调用writewith方法,我们就能根据响应结果做一些自己的处理
                    @Override
                    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                        // log.info("body instanceof Flux: {}", (body instanceof Flux));
                        // 判断响应体是否是Flux类型
                        if (body instanceof Flux) {
                            Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                            // 往返回值里面写数据
                            // 返回一个处理后的请求体
                            // 拼接字符串，把缓冲区中的数据取出来，一点一点拼接好
                            return super.writeWith(
                                    fluxBody.map(dataBuffer -> {
                                        // 调用成功，接口调用次数+1-RPC远程调用
                                        try {
                                            innerUserInvokeInterfService.invokeCount(userId, interfId);
                                        } catch (Exception e) {
                                            log.error("invokeCount", e);
                                        }
                                        // 读取响应体的内容，并转化为字节数组
                                        byte[] content = new byte[dataBuffer.readableByteCount()];
                                        dataBuffer.read(content);
                                        // 释放内存
                                        DataBufferUtils.release(dataBuffer);
                                        // 构建日志
                                        StringBuilder sb2 = new StringBuilder(200);
                                        sb2.append("<--- {} {} \n");
                                        List<Object> rspArgs = new ArrayList<>();
                                        rspArgs.add(originalResponse.getStatusCode());
                                        String data = new String(content, StandardCharsets.UTF_8);
                                        sb2.append(data);
                                        // 打印日志
                                        log.info(sb2.toString(), rspArgs.toArray(), data);
                                        // 将处理后的内容重新包装成DataBuffer并返回
                                        return bufferFactory.wrap(content);
                                    }));
                        } else {
                            // 调用失败，返回一个规范的错误码
                            log.error("<--- {} 响应code异常", getStatusCode());
                        }
                        return super.writeWith(body);
                    }
                };
                // 将装饰过的响应对象传递给下一个过滤器链，并继续处理（设置response对象为装饰过的）
                return chain.filter(exchange.mutate().response(decoratedResponse).build());
            }
            // 返回数据，降级处理
            return chain.filter(exchange);
        } catch (Exception e) {
            this.log.error("gateway log exception.\n" + e);
            return chain.filter(exchange);
        }
    }

    /**
     * 获取过滤器的顺序值
     *
     * @return 过滤器顺序值，返回-1
     */
    @Override
    public int getOrder() {
        return -1;
    }

    /**
     * 处理无权限的情况
     *
     * @param response 响应对象，用于设置响应
     * @return 字段类型为Mono<Void>的对象，表示异步处理结果
     */
    public Mono<Void> handleNoAuth(ServerHttpResponse response) {
        // 设置响应状态码为403-禁止访问
        response.setStatusCode(HttpStatus.FORBIDDEN);
        // 返回处理完成的响应
        // setComplete方法返回Mono对象，告诉程序，请求处理完成了，不需要再执行其他操作了
        return response.setComplete();
    }

    /**
     * 处理调用错误的情况
     *
     * @param response 响应对象，用于设置响应
     * @return 字段类型为Mono<Void>的对象，表示异步处理结果
     */
    public Mono<Void> handleInvokeError(ServerHttpResponse response) {
        // 设置响应状态码为500-服务器内部错误
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        // 返回处理完成的响应
        // setComplete方法返回Mono对象，告诉程序，请求处理完成了，不需要再执行其他操作了
        return response.setComplete();
    }
}
