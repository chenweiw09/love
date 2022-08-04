package com.book.wx.shared.util;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import lombok.SneakyThrows;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

/**
 * 二维码生成工具类
 */
public class QRCodeUtil {
    // private static final String FORMAT_NAME = "JPG";
    private static final String FORMAT_NAME = "PNG";

    // 二维码颜色 默认是黑色
    private static final int QRCOLOR = 0xFF000000;
    // 背景颜色
    private static final int BGWHITE = 0xFFFFFFFF;

    // 加文字二维码高
    private static final int WORD_HEIGHT = 45;

    private static final int QRCODE_SIZE = 300;
    // LOGO宽度
    private static final int LOGO_WIDTH = 60;
    // LOGO高度
    private static final int LOGO_HEIGHT = 60;
    // 加文字二维码高

    private static final String TMP_PATH = "/tmp/";

    private static HashMap hints = new HashMap();

    static {
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 1);
    }


    public static void main(String[] args) {
        String content = "http://www.baidu.com";
        BufferedImage image = createQRImage(content);

//        image = insertWords(image, "我是好人");
//        String logoPath = "/tmp/logo.jpg";
        BufferedImage logo = compressImage("/tmp/mv.jpeg", LOGO_WIDTH, LOGO_HEIGHT);
        insertLogo(logo, image);
        // 做图片压缩
        try {
            String fileName = new Random().nextInt(99999999) + ".jpg";
            saveImage(fileName, image);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    /**
     * 反解析二维码图片
     * @param file
     * @return
     * @throws Exception
     */
    public static String getQrContent(File file) throws Exception {
        BufferedImage image;
        image = ImageIO.read(file);
        if (image == null) {
            return null;
        }
        BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(image);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        Result result = new MultiFormatReader().decode(bitmap, hints);
        String resultStr = result.getText();
        return resultStr;
    }

    /**
     * 创建二维码图片
     * @param content
     * @param logoPath
     * @param words
     * @return
     */
    public static BufferedImage createImage(String content, String logoPath, String words) {
        BufferedImage image = createQRImage(content);
        BufferedImage logo = compressImage(logoPath, LOGO_WIDTH, LOGO_HEIGHT);
        insertLogo(logo, image);
        insertWords(image, words);
        return image;
    }


    @SneakyThrows
    private static String saveImage(String fileName, BufferedImage image) {
        if (!fileName.endsWith(".jpg") && !fileName.endsWith(".png") && !fileName.endsWith("jpeg")) {
            throw new RuntimeException("fileName format error");
        }
        File file = new File(TMP_PATH);
        if (!file.exists() && !file.isDirectory()) {
            file.mkdirs();
        }
        image.flush();
        String fullName = TMP_PATH + fileName;
        ImageIO.write(image, FORMAT_NAME, new File(fullName));
        return fullName;
    }


    // 绘制二维码
    @SneakyThrows
    private static BufferedImage createQRImage(String content) {
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, QRCODE_SIZE, QRCODE_SIZE, hints);
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, bitMatrix.get(x, y) ? QRCOLOR : BGWHITE);
            }
        }
        return image;
    }

    @SneakyThrows
    private static BufferedImage compressImage(String imgPath, int wide, int high) {
        if(imgPath == null || imgPath.equals("")){
            return null;
        }
        File file = new File(imgPath);
        if (!file.exists()) {
            return null;
        }

        BufferedImage src = ImageIO.read(file);
        int width = src.getWidth(null);
        int height = src.getHeight(null);

        boolean needCompress = false;
        if (width > wide) {
            width = wide;
            needCompress = true;
        }
        if (height > high) {
            height = high;
            needCompress = true;
        }

        if (needCompress) { // 压缩LOGO
            Image image = src.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics g = tag.getGraphics();
            g.drawImage(image, 0, 0, null); // 绘制缩小后的图
            g.dispose();

            src = tag;
        }
        return src;
    }

    private static BufferedImage insertWords(BufferedImage image, String words) {
        if (words == null || words.length() == 0) {
            return image;
        }

        //创建一个带透明色的BufferedImage对象
        BufferedImage outImage = new BufferedImage(image.getWidth(), image.getHeight() + WORD_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D outg = outImage.createGraphics();
        setGraphics2D(outg);
        // 画二维码到新的面板
        outg.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);

        // 字体颜色黑色
        Color color = new Color(0, 0, 0);
        outg.setColor(color);
        // 字体、字型、字号
        if (words.length() > 8) {
            outg.setFont(new Font("微软雅黑", Font.PLAIN, 25));
        } else {
            outg.setFont(new Font("微软雅黑", Font.PLAIN, 30));
        }

        //文字长度
        int strWidth = outg.getFontMetrics().stringWidth(words);
        //总长度减去文字长度的一半  （居中显示）
        int wordStartX = (image.getWidth() - strWidth) / 2;
        //height + (outImage.getHeight() - height) / 2 + 12
        int wordStartY = image.getHeight() + 30;
        // 画文字
        outg.drawString(words, wordStartX, wordStartY);
        outg.dispose();
        outImage.flush();
        return outImage;
    }

    private static BufferedImage insertLogo(BufferedImage logoImage, BufferedImage qrImage){
        if(logoImage == null){
            return qrImage;
        }

        // 构建绘图对象
        Graphics2D g = qrImage.createGraphics();
        setGraphics2D(g);
        // 开始绘制logo图片 等比缩放：（width * 2 / 10 height * 2 / 10）不需缩放直接使用图片宽高
        g.drawImage(logoImage, qrImage.getWidth() * 2 / 5, qrImage.getHeight() * 2 / 5, logoImage.getWidth(), logoImage.getHeight(), null);
        Shape shape = new RoundRectangle2D.Float(qrImage.getWidth() * 2 / 5, qrImage.getHeight() * 2 / 5, logoImage.getWidth(), logoImage.getHeight(),6, 6);
        g.setStroke(new BasicStroke(3f));
        g.draw(shape);
        // 释放资源
        g.dispose();
        logoImage.flush();
        return qrImage;
    }
    /**
     * 设置 Graphics2D 属性  （抗锯齿）
     *
     * @param graphics2D
     */
    private static void setGraphics2D(Graphics2D graphics2D) {
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_DEFAULT);
        Stroke s = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
        graphics2D.setStroke(s);
    }

}
