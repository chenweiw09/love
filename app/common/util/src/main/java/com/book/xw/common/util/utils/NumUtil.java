package com.book.xw.common.util.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

public class NumUtil {

    /**
     * 生成商户订单号
     * @return String
     */
    public static String mchOrderNo(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String date = sdf.format(new Date());

        Random random = new Random();
        String fourRandom = String.valueOf(random.nextInt(10000));
        int randLength = fourRandom.length();
        //不足4位继续补充
        if(randLength<4){
            for(int remain = 1; remain <= 4 - randLength; remain ++ ){
                fourRandom += random.nextInt(10)  ;
            }
        }
        return date+fourRandom;
    }

    /**
     * 获取随机字符串 Nonce Str
     * @return String 随机字符串
     */
    public static String getNonceStr() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 32);
    }

    public static String getRandomStr(int length) {
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        int randomNum;
        char randomChar;
        Random random = new Random();
        // StringBuffer类型的可以append增加字符
        StringBuffer str = new StringBuffer();
        for (int i = 0; i < length; i++) {
            // 可生成[0,n)之间的整数，获得随机位置
            randomNum = random.nextInt(base.length());
            // 获得随机位置对应的字符
            randomChar = base.charAt(randomNum);
            // 组成一个随机字符串
            str.append(randomChar);
        }
        return str.toString().toUpperCase();
    }
}
