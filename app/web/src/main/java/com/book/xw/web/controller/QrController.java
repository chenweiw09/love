package com.book.xw.web.controller;

import com.book.wx.shared.util.QRCodeUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;

@RequestMapping("/qr")
@Controller
public class QrController {


    @RequestMapping("/code")
    public void qrcode(@RequestParam String content, HttpServletResponse response) throws Exception {
        ServletOutputStream out = response.getOutputStream();
        try {
            BufferedImage qrImage = QRCodeUtil.createImage(content, null, null);
            ImageIO.write(qrImage, "jpg", response.getOutputStream());
        } catch (Exception e) {
            throw new Exception("生成二维码失败！");
        } finally {
            if (out != null) {
                out.flush();
                out.close();
            }
        }
    }

    @GetMapping("/index")
    public String qrIndex(){
        return "admin/qrcode";
    }

}
