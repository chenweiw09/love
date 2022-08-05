package com.book.xw.web.controller;

import com.book.xw.core.model.entity.FileRecord;
import com.book.xw.core.model.repository.FileRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.UUID;

@Controller
@RequestMapping("/file")
public class FileDownloadController {

    private static final String LOCAL_PATH = "/tmp/mytest/";

    @Autowired
    private FileRecordRepository recordRepository;


    @GetMapping("/index")
    public String fileDownloadIndex(){
        return "admin/index";
    }


    @RequestMapping("/download")
    public void downloadLocal(@RequestParam String fileName, HttpServletResponse response) throws IOException {
        // 读到流中
        File file = new File(LOCAL_PATH + fileName);
        if (!file.exists()) {
            throw new RuntimeException(fileName + "not found");
        }

        InputStream inputStream = new FileInputStream(LOCAL_PATH + fileName);
        response.reset();
        response.setContentType("application/octet-stream");
        response.addHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
        ServletOutputStream outputStream = response.getOutputStream();
        byte[] b = new byte[1024];
        int len;
        //从输入流中读取一定数量的字节，并将其存储在缓冲区字节数组中，读到末尾返回-1
        while ((len = inputStream.read(b)) > 0) {
            outputStream.write(b, 0, len);
        }
        inputStream.close();
    }

    @PostMapping("/upload")
    @ResponseBody
    public String uploadFile(@RequestParam MultipartFile file, HttpServletRequest request) throws IOException {
        if (file.isEmpty()) {
            throw new RuntimeException("file empty error");
        }

        String originFileName = file.getOriginalFilename();
        // 如果目录不存在则创建
        File uploadDir = new File(LOCAL_PATH);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        String suffixName = originFileName.substring(originFileName.lastIndexOf("."));//获取文件后缀名
        //重新随机生成名字
        String filename = UUID.randomUUID().toString().replace("-","") + suffixName;
        File localFile = new File(LOCAL_PATH + filename);
        file.transferTo(localFile); //把上传的文件保存至本地
        // 文件保存成功后存入数据库
        FileRecord record = new FileRecord();
        record.setFileName(filename);
        record.setOriginFileName(originFileName);
        record.setDesc("test file");
        record.setDirPath(localFile.getAbsolutePath());
        record.setFileSize(localFile.length()/1024/1024.0);
        recordRepository.saveFileRecord(record);
        return originFileName;
    }


}
