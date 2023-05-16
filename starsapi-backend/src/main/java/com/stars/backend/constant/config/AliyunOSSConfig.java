package com.stars.backend.constant.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.auth.CredentialsProviderFactory;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 阿里云OSS配置
 * 用于配置阿里云对象存储服务的相关属性，包括访问凭证、存储桶名称等。
 * 配置属性通过配置文件中的 "aliyun.oss" 前缀进行设置。
 * 示例配置文件片段：
 * aliyun.oss.endPoint=yourEndPoint
 * aliyun.oss.accessKeyId=yourAccessKeyId
 * aliyun.oss.accessKeySecret=yourAccessKeySecret
 * aliyun.oss.bucketName=yourBucketName
 * aliyun.oss.urlPrefix=yourUrlPrefix
 * aliyun.oss.fileHost=yourFileHost
 *
 * @author stars
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "aliyun.oss")
public class AliyunOSSConfig {

    /**
     * OSS服务的EndPoint
     */
    private String endPoint;

    /**
     * 访问OSS的AccessKeyId
     */
    private String accessKeyId;

    /**
     * 访问OSS的AccessKeySecret
     */
    private String accessKeySecret;

    /**
     * OSS存储桶名称
     */
    private String bucketName;

    /**
     * OSS访问URL的前缀
     */
    private String urlPrefix;

    /**
     * 文件的主机名或路径
     */
    private String fileHost;

    /**
     * 创建OSS客户端Bean
     *
     * @return OSS 客户端对象
     */
    @Bean
    public OSS ossClient() {
        DefaultCredentialProvider credentialsProvider
                = CredentialsProviderFactory.newDefaultCredentialProvider(this.accessKeyId, this.accessKeySecret);
        return (new OSSClientBuilder()).build(this.endPoint, credentialsProvider);
    }
}
