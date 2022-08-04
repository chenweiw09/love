package com.book.xw.common.util.constants;

/**
 * 微信公众号常量类
 */
public class WxConstants {
    /**
     * 默认编码
     */
    public static final String DEFAULT_CHARSET = "UTF-8";

    /**
     * GET请求方式
     */
    public static final String METHOD_GET = "GET";
    /**
     * POST请求方式
     */
    public static final String METHOD_POST = "POST";

    public static final Integer CONNECTION_TIMEOUT = 3000;

    public static Integer READ_TIMEOUT = 5000;
    /**
     * 统一下单-扫描支付
     */
    public static String PAY_UNIFIEDORDER = "https://api.mch.weixin.qq.com/pay/unifiedorder";
    public static String PAY_QUERY = "https://api.mch.weixin.qq.com/pay/orderquery";
    public static String CLOSE_ORDER = "https://api.mch.weixin.qq.com/pay/closeorder";
    /**
     * 请求成功返回码
     */
    public final static String ERRCODE_OK_CODE = "0";
    /**
     * 错误的返回码的Key
     */
    public final static String ERRCODE = "errcode";

    /**
     * 返回状态码
     */
    public final static String RETURN_CODE= "return_code";

    public final static String SUCCESS_CODE = "SUCCESS";
    public final static String FAIL_CODE = "FAIL";
    public final static String PROCESSING = "PROCESSING";

    /**
     * access_token 字符串
     */
    public final static String ACCESS_TOKEN = "access_token";

    /**
     * 签名类型 MD5
     */
    public final static String SING_MD5 = "MD5";

    /**
     * 签名类型 HMAC-SHA256
     */
    public final static String SING_HMACSHA256 = "HMAC-SHA256";

    /**
     *@description: 获取code
     */
    public static String GET_CODE="https://open.weixin.qq.com/connect/oauth2/authorize?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=SCOPE&state=STATE#wechat_redirect";


    /**
     * 获取ACCESS_TOKEN接口
     */
    public static String GET_ACCESSTOKEN_URL="https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";

}
