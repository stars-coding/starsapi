package com.stars.interfaces.controller;

import cn.hutool.json.JSONUtil;
import com.stars.interfaces.common.ErrorResponse;
import com.stars.interfaces.model.entity.TestUser;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 名称控制器
 *
 * @author stars
 */
@RestController
@RequestMapping("/name")
public class NameController {

    /**
     * 处理GET请求，根据名称获取结果
     *
     * @param name 字符串，表示名称参数
     * @return 字符串，表示根据名称获取的结果
     */
    @GetMapping("/get")
    public String getNameByGet(String name) {
        return "GET " + name;
    }

    /**
     * 处理POST请求，根据名称获取结果
     *
     * @param name 字符串，表示名称参数
     * @return 字符串，表示根据名称获取的结果
     */
    @PostMapping("/post")
    public String getNameByPost(@RequestParam String name) {
        return "POST " + name;
    }

    /**
     * 处理包含请求体的请求，获取用户名
     *
     * @param user    请求体中的TestUser对象，包含用户名
     * @param request 请求对象，用于获取请求信息
     * @return 字符串，表示请求体中的用户名或错误信息
     */
    @PostMapping("/user")
    public String getUsername(@RequestBody TestUser user, HttpServletRequest request) {
        // 校验用户名称的合法性
        if (user.getUsername() == null || "".equals(user.getUsername())) {
            ErrorResponse errorResponse = new ErrorResponse("用户名为空或空白");
            // 将错误响应转化为JSON格式并返回
            return JSONUtil.toJsonStr(errorResponse);
        }
        // 将用户转换为JSON字符串
        String jsonString = JSONUtil.toJsonStr(user);
        return jsonString;
    }
}
