package com.stars.backend.manager;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;

/**
 * COS管理器
 * 用于上传文件到阿里云对象存储并获取文件访问链接。
 *
 * @author stars
 */
@Component
public class CosManager {

    @Resource
    private OSS ossClient;

    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    @Value("${aliyun.oss.bucketName}")
    private String bucketName;

    /**
     * 获取文件的内容类型
     *
     * @param filenameExtension 文件名扩展名
     * @return 文件的内容类型
     */
    public static String getContentType(String filenameExtension) {
        if (filenameExtension.equalsIgnoreCase("bmp"))
            return "image/bmp";
        if (filenameExtension.equalsIgnoreCase("gif"))
            return "image/gif";
        if (filenameExtension.equalsIgnoreCase("jpeg") || filenameExtension.equalsIgnoreCase("jpg") || filenameExtension
                .equalsIgnoreCase("png"))
            return "image/jpeg";
        if (filenameExtension.equalsIgnoreCase("html"))
            return "text/html";
        if (filenameExtension.equalsIgnoreCase("txt"))
            return "text/plain";
        if (filenameExtension.equalsIgnoreCase("vsd"))
            return "application/vnd.visio";
        if (filenameExtension.equalsIgnoreCase("pptx") || filenameExtension.equalsIgnoreCase("ppt"))
            return "application/vnd.ms-powerpoint";
        if (filenameExtension.equalsIgnoreCase("docx") || filenameExtension.equalsIgnoreCase("doc"))
            return "application/msword";
        if (filenameExtension.equalsIgnoreCase("xml"))
            return "text/xml";
        return "application/octet-stream";
    }

    /**
     * 上传文件到阿里云COS
     *
     * @param instream 文件输入流
     * @param fileName 文件名
     * @return 文件的访问链接
     */
    public String uploadFile2OSS(InputStream instream, String fileName) {
        String ret = "";
        try {
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(instream.available());
            objectMetadata.setCacheControl("no-cache");
            objectMetadata.setHeader("Pragma", "no-cache");
            objectMetadata.setContentType(getContentType(fileName.substring(fileName.lastIndexOf("."))));
            objectMetadata.setContentDisposition("attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
            PutObjectResult putResult = this.ossClient.putObject(this.bucketName, fileName, instream, objectMetadata);
            ret = putResult.getETag();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (instream != null)
                    instream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "http://" + this.bucketName + "." + this.endpoint + "/" + fileName;
    }
}
