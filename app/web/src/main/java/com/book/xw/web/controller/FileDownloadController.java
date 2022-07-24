package com.book.xw.web.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;

@RestController
public class FileDownloadController {

    private static final String LOCAL_PATH = "/home/myfile/";

    @RequestMapping("/download/file")
    public void downloadLocal(@RequestParam String fileName, HttpServletResponse response) throws IOException {
        // 读到流中
        File file = new File(LOCAL_PATH+fileName);
        if(!file.exists()){
            throw new RuntimeException(fileName+"not found");
        }

        InputStream inputStream = new FileInputStream(LOCAL_PATH+fileName);
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
}
