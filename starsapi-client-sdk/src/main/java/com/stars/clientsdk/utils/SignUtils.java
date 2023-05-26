package com.stars.clientsdk.utils;

import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;

/**
 * 签名工具
 *
 * @author stars
 */
public class SignUtils {

    /**
     * 获取签名
     * 签名认证算法，使用SHA1算法
     *
     * @param body
     * @param secretKey
     * @return
     */
    public static String getSign(String body, String secretKey) {
        // 使用SHA1算法的Digester
        Digester digester = new Digester(DigestAlgorithm.SHA1);
        // 构建签名内容
        String content = body + "." + secretKey;
        // 计算摘要签名，并以十六进制表示形式返回
        return digester.digestHex(content);
    }
}
