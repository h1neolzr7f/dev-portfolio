package com.example.project.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelWriter;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.project.common.annotation.AutoLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.project.common.Result;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * </p>
 */
@RestController
@RequestMapping("/file")
@Slf4j
public class FileController {

    private static final String FILES_DIR = "/files/";

    @Value("${server.port:9090}")
    private String port;
    @Value("${file.download.ip:localhost}")
    private String downloadIp;

    /**
     * 文件上传
     *
     * @param file MultipartFile
     * @return Result
     */
    @PostMapping("/upload")
    @AutoLog("上传文件")
    public Result upload(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();  // 文件完整的名称
        String extName = FileUtil.extName(originalFilename);  // 文件后缀名
        String uniFileFlag = IdUtil.fastSimpleUUID();
        String fileFullName = uniFileFlag + StrUtil.DOT + extName;
        // 封装完整的文件路径获取方法
        String fileUploadPath = getFileUploadPath(fileFullName);
        //  完整的上传文件名： D:\知识星球\partner-back/files/1231321321321321982321.jpg
        long size = file.getSize();  // 单位是 byte, size / 1024 -> kb
//        byte[] bytes = file.getBytes();
        String name = file.getName();
        log.info("{}, {}, {}", originalFilename, size, name);

        try {
            java.io.File uploadFile = new java.io.File(fileUploadPath);
            java.io.File parentFile = uploadFile.getParentFile();
            if (!parentFile.exists()) {  // 如果父级不存在，也就是说files目录不存在，那么我要创建出来
                parentFile.mkdirs();
            }
            file.transferTo(uploadFile);
        } catch (Exception e) {
            log.error("文件上传失败", e);
            return Result.error("文件上传失败");
        }

        String url = "http://" + downloadIp + ":" + port + "/file/download/" + fileFullName;
        return Result.success(url);
    }

    @AutoLog("富文本-上传图片")
    @PostMapping("/uploadImg")
    public Dict uploadImg(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();  // 文件完整的名称
        String extName = FileUtil.extName(originalFilename);  // 文件后缀名
        String uniFileFlag = IdUtil.fastSimpleUUID();
        String fileFullName = uniFileFlag + StrUtil.DOT + extName;
        // 封装完整的文件路径获取方法
        String fileUploadPath = getFileUploadPath(fileFullName);
        long size = file.getSize();  // 单位是 byte, size / 1024 -> kb
//        byte[] bytes = file.getBytes();
        String name = file.getName();
        log.info("{}, {}, {}", originalFilename, size, name);
        String url = "";

        try {
            java.io.File uploadFile = new java.io.File(fileUploadPath);
            java.io.File parentFile = uploadFile.getParentFile();
            if (!parentFile.exists()) {  // 如果父级不存在，也就是说files目录不存在，那么我要创建出来
                parentFile.mkdirs();
            }
            file.transferTo(uploadFile);
        } catch (Exception e) {
            log.error("文件上传失败", e);
            return Dict.create().set("errno", 1);
        }

        url = "http://" + downloadIp + ":" + port + "/file/download/" + fileFullName;
        Dict dict = Dict.create().set("errno", 0).set("data", Dict.create().set("url", url));
        return dict;
    }


    /**
     * 文件下载
     *
     * @param fileFullName
     * @param response
     * @throws IOException
     */
    @GetMapping("/download/{fileFullName}")
    @AutoLog("下载文件")
    public void downloadFile(@PathVariable String fileFullName,
                             HttpServletResponse response) throws IOException {
        String fileUploadPath = getFileUploadPath(fileFullName);
        byte[] bytes = FileUtil.readBytes(fileUploadPath);
        response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileFullName, "UTF-8"));  // 附件下载
        OutputStream os = response.getOutputStream();
        os.write(bytes);
        os.flush();
        os.close();
    }

    /**
     * 获取文件的完整路径
     *
     * @param fileFullName
     * @return
     */
    private String getFileUploadPath(String fileFullName) {
        File workDir = new File(System.getProperty("user.dir"));
        File filesDir = new File(workDir, FILES_DIR);
        File parentFilesDir = new File(workDir.getParentFile() == null ? workDir : workDir.getParentFile(), FILES_DIR);
        if (!filesDir.exists() && parentFilesDir.exists()) {
            filesDir = parentFilesDir;
        }
        return new File(filesDir, fileFullName).getAbsolutePath();
    }

}
