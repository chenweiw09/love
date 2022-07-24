package com.book.xw.common.util.utils;

import com.aspose.pdf.Document;
import com.aspose.pdf.SaveFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;

public class Pdf2DocUtil {
    private static Logger log = LoggerFactory.getLogger(Pdf2DocUtil.class);

    //pdf转doc
    public static void pdf2doc(String pdfPath) {
        long start = System.currentTimeMillis();
        log.info("pdf2doc, pdfPath:{}", pdfPath);
        try {
            //新建一个word文档
            String wordPath = pdfPath.substring(0, pdfPath.lastIndexOf(".")) + ".docx";
            log.info("pdf2doc, wordPath:{}", wordPath);
            FileOutputStream os = new FileOutputStream(wordPath);
            //doc是将要被转化的word文档
            Document doc = new Document(pdfPath);
            doc.save(os, SaveFormat.DocX);
            os.close();
            //转化用时
            long now = System.currentTimeMillis();
            log.info("Pdf 转 Word 共耗时：" + ((now - start) / 1000.0) + "秒");
        } catch (Exception e) {
            log.error("Pdf 转 Word 失败...", e);
        }
    }
}
