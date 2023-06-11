package com.stars.interfaces.controller;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.stars.interfaces.common.ErrorResponse;
import com.stars.interfaces.model.entity.QYKParams;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 青云客机器人控制器
 *
 * @author stars
 */
@RestController
@RequestMapping("/qyk")
public class QYKController {

    /**
     * 处理POST请求，根据请求参数获取IP
     *
     * @param qykParams 请求体中的QYKParams对象，包含请求参数
     * @param request   请求对象，用于获取请求信息
     * @return 字符串，表示根据请求参数获取的IP结果
     */
    @PostMapping("/reply")
    public String getIPByPost(@RequestBody QYKParams qykParams, HttpServletRequest request) {
        String msg = qykParams.getContent();
        // 校验参数的合法性
        if ("".equals(msg) || msg == null) {
            ErrorResponse errorResponse = new ErrorResponse("请求参数为空或空白");
            // 将错误响应转化为JSON格式并返回
            return JSONUtil.toJsonStr(errorResponse);
        }
        String url = "http://api.qingyunke.com/api.php?key=free&appid=0&msg=" + msg;
        HttpResponse response = HttpUtil.createGet(url).execute();
        String result = response.body();
        return result;
    }
}
