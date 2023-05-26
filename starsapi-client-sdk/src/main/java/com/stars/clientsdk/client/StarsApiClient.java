package com.stars.clientsdk.client;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.stars.clientsdk.utils.SignUtils;
import com.stars.common.model.entity.User;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * API客户端
 * StarsApiClient是一个用于与API网关交互的客户端类。
 *
 * @author stars
 */
public class StarsApiClient {

    // todo 开发环境-本地地址-网关地址
    private static final String GATEWAY_HOST = "http://localhost:28002";
    // todo 线上环境-服务器公网地址-网关地址
//    private static final String GATEWAY_HOST = "";

    /**
     * 公钥
     */
    private String accessKey;

    /**
     * 私钥
     */
    private String secretKey;

    /**
     * 构造函数
     * 构造StarsApiClient的新实例。
     *
     * @param accessKey 公钥
     * @param secretKey 密钥
     */
    public StarsApiClient(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    /**
     * 使用GET请求获取名称
     *
     * @param name 要获取的名称
     * @return 获取的名称
     */
    public String getNameByGet(String name) {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", name);
        String result = HttpUtil.get(com.stars.clientsdk.client.StarsApiClient.GATEWAY_HOST + "/api/name/", paramMap);
        return result;
    }

    /**
     * 使用POST请求获取名称
     *
     * @param name 要获取的名称
     * @return 获取的名称
     */
    public String getNameByPost(String name) {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", name);
        String result = HttpUtil.post(com.stars.clientsdk.client.StarsApiClient.GATEWAY_HOST + "/api/name/", paramMap);
        return result;
    }

    /**
     * 获取用户名称
     *
     * @param user 包含用户信息的User对象
     * @return 用户名称
     */
    public String getUsername(User user) {
        // 将用户转换为JSON参数
        String json = JSONUtil.toJsonStr(user);
        // 构建POST请求的URL
        String baseUrl = com.stars.clientsdk.client.StarsApiClient.GATEWAY_HOST + "/name/user";
        HttpResponse httpResponse = HttpRequest.post(baseUrl)
                // 设置请求头部信息-自定义头部MAP
                .addHeaders(this.getHeadMap(json, true))
                // 将JSON参数作为请求体
                .body(json)
                // 将构建好的HTTP的POST请求发送到指定的URL地址
                .execute();
        // 返回HTTP响应
        return httpResponse.body();
    }

    /**
     * 在线调用
     * 通过HTTP请求，远程调用接口。
     *
     * @param parameters 包含参数的字符串
     * @param url        调用的URL路径
     * @param method     请求方法 (GET或POST)
     * @return 调用结果
     */
    public String onlineInvoke(String parameters, String url, String method) {
        if ("POST".equals(method)) {
            // 构建POST请求的URL
            String baseUrl = com.stars.clientsdk.client.StarsApiClient.GATEWAY_HOST + url;
            // 发起POST请求
            HttpResponse httpResponse = HttpRequest.post(baseUrl)
                    // 设置请求头部信息-自定义头部MAP
                    .addHeaders(this.getHeadMap(parameters, true))
                    // 将参数作为请求体
                    .body(parameters)
                    // 将构建好的HTTP的POST请求发送到指定的URL地址
                    .execute();
            // 返回HTTP响应
            return httpResponse.body();
        }
        if ("GET".equals(method)) {
            // 解析请求参数
            HashMap<String, String> stringObjectHashMap = this.handleParameters(parameters);
            // 构建GET请求的URL
            String baseUrl = com.stars.clientsdk.client.StarsApiClient.GATEWAY_HOST + url + "?" + this.getEncodedParams(stringObjectHashMap);
            // 发起GET请求
            HttpRequest httpRequest = HttpRequest.get(baseUrl)
                    // 设置请求头部信息-自定义头部MAP
                    .addHeaders(this.getHeadMap(parameters, false));
            // 将构建好的HTTP的GET请求发送到指定的URL地址
            HttpResponse httpResponse = httpRequest.execute();
            // 返回HTTP响应
            return httpResponse.body();
        }
        return "不支持 " + method + " 请求方法";
    }

    /**
     * 获取请求头信息
     *
     * @param body   请求体
     * @param isPost 是否为POST请求
     * @return 请求头信息的哈希映射
     */
    private Map<String, String> getHeadMap(String body, boolean isPost) {
        // 如果是POST请求，必须指定UTF-8编码格式
        String encode = isPost ? this.encodeBody(body) : body;
        // 自定义头部MAP内容
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("accessKey", this.accessKey);
        hashMap.put("body", encode);
        hashMap.put("nonce", RandomUtil.randomNumbers(4));
        // 当前系统时间戳。系统毫秒数，除以1000为秒，最后转换为数字字符串
        hashMap.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000L));
        hashMap.put("sign", SignUtils.getSign(encode, this.secretKey));
        return hashMap;
    }

    /**
     * 译码请求体
     * 保证请求的准确性和可靠性。
     *
     * @param body 请求体
     * @return 编码后的请求体
     */
    private String encodeBody(String body) {
        try {
            // 对body指定UTF-8编码格式
            return URLEncoder.encode(body, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 处理参数字符串
     * 将URL参数字符串编码为哈希映射。
     *
     * @param parameters 包含参数的字符串
     * @return 哈希映射
     */
    private HashMap<String, String> handleParameters(String parameters) {
        HashMap<String, String> map = new HashMap<>(32);
        String[] split = parameters.split("&");
        for (String s : split) {
            String[] split1 = s.split("=");
            String a = split1[0];
            String b = split1[1];
            map.put(a, b);
        }
        return map;
    }

    /**
     * 获取译码参数
     * 将哈希映射编码为URL参数字符串。
     *
     * @param params 参数哈希映射
     * @return 编码后的参数字符串
     */
    private String getEncodedParams(Map<String, String> params) {
        try {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                String encodedKey = URLEncoder.encode(key, "UTF-8");
                String encodedValue = URLEncoder.encode(value, "UTF-8");
                sb.append(encodedKey).append("=").append(encodedValue).append("&");
            }
            return sb.substring(0, sb.length() - 1);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
