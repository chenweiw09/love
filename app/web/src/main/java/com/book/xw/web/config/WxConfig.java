package com.book.xw.web.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;

/**
 * 微信公众号开发配置类
 */
@Component
@Data
public class WxConfig {
    /**
     * 开发者ID
     */
    @Value("${wx.appID}")
    public String appID;


    /**
     * 开发者密码
     */
    @Value("${wx.appSecret}")
    public String appSecret;

    /**
     * 商户号
     */
    @Value("${wx.mchID}")
    public String mchID;

    /**
     * API密钥
     */
    @Value("${wx.key}")
    public String key;

    @Value("${wx.server.token}")
    public String serverToken;

    /**
     * 统一下单-通知链接
     */
    @Value("${wx.unifiedorder.notifyUrl}")
    public String unifiedorderNotifyUrl;

}
