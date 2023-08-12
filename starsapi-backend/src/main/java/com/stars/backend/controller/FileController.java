package com.stars.backend.controller;

import com.stars.backend.common.BaseResponse;
import com.stars.backend.common.ResultUtils;
import com.stars.backend.manager.CosManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文件上传控制器
 *
 * @author stars
 */
@RestController
@Slf4j
@RequestMapping("/file")
public class FileController {

    // 用于分隔多个文件相关信息的分隔符
    private static final String FILE_DELIMITER = ",";

    @Resource
    private CosManager cosManager;

    /**
     * 单文件上传
     *
     * @param file    上传的文件
     * @param request 请求对象
     * @return 包含上传结果的响应对象
     * @throws IOException 如果上传过程中出现IO错误
     */
    @PostMapping("/upload")
    public BaseResponse<Map<String, Object>> uploadFile(@RequestBody MultipartFile file, HttpServletRequest request)
            throws IOException {
        Map<String, Object> result = new HashMap<>(2);
        // 生成一个随机UUID
        String uuid = RandomStringUtils.randomAlphanumeric(8);
        // 构建新的文件名
        String fileName = uuid + "-" + file.getOriginalFilename();
        // 调用CosManager上传文件到OSS
        String imageUrl = this.cosManager.uploadFile2OSS(file.getInputStream(), fileName);
        // 存储上传后的文件URL
        result.put("url", imageUrl);
        // 返回一个成功的响应，响应体中携带result信息
        return ResultUtils.success(result);
    }

    /**
     * 多文件上传
     *
     * @param files 上传的文件列表
     * @return 包含上传结果的响应对象
     * @throws Exception 如果上传文件失败
     */
    @PostMapping("/uploads")
    public BaseResponse<Map<String, Object>> uploadFiles(List<MultipartFile> files) throws Exception {
        try {
            // 存储上传后的文件URL列表
            List<String> urls = new ArrayList<>();
            // 存储上传后的文件名列表
            List<String> fileNames = new ArrayList<>();
            // 存储原始文件名列表
            List<String> originalFilenames = new ArrayList<>();
            // 遍历上传的文件列表
            for (MultipartFile file : files) {
                // 生成一个随机UUID
                String uuid = RandomStringUtils.randomAlphanumeric(8);
                // 构建新的文件名
                String fileName = uuid + "-" + file.getOriginalFilename();
                // 调用CosManager上传文件到OSS
                String imageUrl = this.cosManager.uploadFile2OSS(file.getInputStream(), fileName);
                // 存储上传后的文件URL
                urls.add(imageUrl);
                // 存储上传后的文件名
                fileNames.add(fileName);
                // 存储原始文件名
                originalFilenames.add(file.getOriginalFilename());
            }
            Map<String, Object> result = new HashMap<>();
            // 将文件相关信息以分隔符分隔并存储到结果Map中
            result.put("urls", StringUtils.join(urls, this.FILE_DELIMITER));
            result.put("fileNames", StringUtils.join(fileNames, this.FILE_DELIMITER));
            result.put("originalFilenames", StringUtils.join(originalFilenames, this.FILE_DELIMITER));
            // 返回一个成功的响应，响应体中携带result信息
            return ResultUtils.success(result);
        } catch (Exception e) {
            throw new Exception("文件上传失败");
        }
    }
}
