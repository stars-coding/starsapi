package com.stars.interfaces.controller;

import com.stars.interfaces.model.entity.AvatarParams;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 头像控制器
 *
 * @author stars
 */

@RestController
@RequestMapping("/avatar")
public class AvatarController {

    /**
     * 处理POST请求，获取头像URL
     *
     * @param avatarParams 请求体中的AvatarParams对象，可选，包含请求参数
     * @param request      请求对象，用于获取请求信息
     * @return 字符串，表示头像URL
     * @throws Exception 可能抛出的异常
     */
    @PostMapping("/avatarUrl")
    public String getAvatarUrlByPost(@RequestBody(required = false) AvatarParams avatarParams, HttpServletRequest request)
            throws Exception {
        String avatarUrl = "https://www.loliapi.com/acg/pp/";
        String redirectUrl = this.getRedirectUrl(avatarUrl);
        return redirectUrl;
    }

    /**
     * 获取重定向URL
     *
     * @param path 重定向路径
     * @return 字符串，表示重定向URL
     * @throws IOException 可能抛出的I/O异常
     */
    private String getRedirectUrl(String path) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(path).openConnection();
        conn.setInstanceFollowRedirects(false);
        conn.setConnectTimeout(5000);
        String location = conn.getHeaderField("Location");
        return location;
    }
}
