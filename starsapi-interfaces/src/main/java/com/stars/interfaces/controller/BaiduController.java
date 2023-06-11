package com.stars.interfaces.controller;

import cn.hutool.http.HttpUtil;
import com.stars.interfaces.model.entity.BaiduParams;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

/**
 * 百度控制器
 *
 * @author stars
 */

@RestController
@RequestMapping("/baidu")
public class BaiduController {

    /**
     * 处理POST请求，获取百度信息
     *
     * @param baiduParams 请求体中的BaiduParams对象，可选，包含请求参数
     * @param request     请求对象，用于获取请求信息
     * @return 字符串，表示百度信息的结果
     */
    @PostMapping("/baiduInfo")
    public String getBaiduInfoByPost(@RequestBody(required = false) BaiduParams baiduParams, HttpServletRequest request) {
        String baiduUrl = "https://www.coderutil.com/api/resou/v1/baidu";
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("access-key", "2a73055beafb826cf0aaf0d284d9eede");
        paramMap.put("secret-key", "3fe196bd0a439eef303155b3870b71d5");
        if (baiduParams != null && baiduParams.getSize() > 0) {
            paramMap.put("size", baiduParams.getSize());
        } else {
            paramMap.put("size", 10);
        }
        String result = HttpUtil.get(baiduUrl, paramMap);
        return result;
    }
}
