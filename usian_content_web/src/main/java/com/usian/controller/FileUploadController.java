package com.usian.controller;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.usian.utils.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/file")
public class FileUploadController {
    @Autowired
    private FastFileStorageClient fastFileStorageClient;

    private static final List<String> CONTENT_TYPES = Arrays.asList("image/jpeg", "image/gif", "png");

    @RequestMapping("/upload")
    public Result upload(MultipartFile file) throws IOException {
        // 获取文件名称
        String originalFilename = file.getOriginalFilename();
        // 查看文件类型
        String contentType = file.getContentType();
        if (!CONTENT_TYPES.contains(contentType)) {
            return Result.error("文件类型不合法:" + originalFilename);
        }
        // 查看文件内容
        BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
        if (bufferedImage == null) {
            return Result.error("文件内容不合法：" + originalFilename);
        }
        // 上传图片
        String ext = StringUtils.substringAfterLast(originalFilename, ".");
        StorePath storePath = fastFileStorageClient.uploadFile(file.getInputStream(), file.getSize(), ext, null);
        // 返回图片路径
        String fullPath = storePath.getFullPath();
        return Result.ok("http://image.usian.com/" + fullPath);
    }
}
