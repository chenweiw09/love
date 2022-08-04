package com.book.wx.shared.service;

import com.alibaba.fastjson.JSONObject;
import com.book.wx.shared.propertyconfig.WxConfig;
import com.book.wx.shared.util.HttpsClient;
import com.book.xw.common.util.utils.NumUtil;
import com.book.wx.shared.util.SHA1Util;
import com.book.wx.shared.util.WxUtil;
import com.book.xw.core.model.entity.AccessToken;
import com.book.xw.common.util.constants.WxConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 微信菜单实现类类
 */
@Service
@Slf4j
public class WxMenuService {

    @Autowired
    private WxConfig wxConfig;

    private Map<String, String> prePayCache = new ConcurrentHashMap<>();

    private Map<String, String> payResultCache = new ConcurrentHashMap<>();


    // api文档地址https://pay.weixin.qq.com/wiki/doc/api/native.php?chapter=9_1
    public String wxPayUrl(Double totalFee, String outTradeNo, String signType) throws Exception {
        HashMap<String, String> data = new HashMap<String, String>();
        //公众账号ID
        data.put("appid", wxConfig.getAppID());
        //商户号
        data.put("mch_id", wxConfig.getMchID());
        // 设备信息，非必须
        data.put("device_info", "");
        //随机字符串
        data.put("nonce_str", NumUtil.getNonceStr());
        //商品描述
        data.put("body", "测试支付");
        //商户订单号
        data.put("out_trade_no", outTradeNo);
        //标价币种
        data.put("fee_type", "CNY");
        //标价金额，单位是分
        data.put("total_fee", String.valueOf(Math.round(totalFee * 100)));
        //用户的IP
        data.put("spbill_create_ip", "123.12.12.123");
        //通知地址
        data.put("notify_url", wxConfig.getUnifiedorderNotifyUrl());
        //交易类型
        data.put("trade_type", "NATIVE");//JSAPI /NATIVE/APP
        //用户oppenId，trade_type=JSAPI时（即JSAPI支付），此参数必传
//        data.put("openid","oUpF8uMuAJO_M2pxb1Q9zNjWeS6o");
        //签名类型
        data.put("sign_type", signType);
        //签名
        data.put("sign", WxUtil.getSignature(data, wxConfig.getKey(), signType));

        String requestXML = WxUtil.mapToXml(data);
        String responseString = HttpsClient.httpsRequestReturnString(WxConstants.PAY_UNIFIEDORDER, WxConstants.METHOD_POST, requestXML);
        Map<String, String> resultMap = WxUtil.processResponseXml(responseString, signType, wxConfig.getKey());
        log.info("微信支付二维码返回:{}", JSONObject.toJSONString(resultMap));
        if (resultMap.get(WxConstants.RETURN_CODE).equals(WxConstants.SUCCESS_CODE)) {
            // 要缓存prepay_id
            prePayCache.put(outTradeNo, resultMap.get("prepay_id"));
            return resultMap.get("code_url");
        }
        return null;
    }

    /**
     * 查询订单状态
     *
     * @param outTradeNo
     * @param signType
     * @return
     * @throws Exception
     */
    public String queryOrder(String outTradeNo, String signType) throws Exception {
        if (payResultCache.containsKey(outTradeNo) && (payResultCache.get(outTradeNo).equals(WxConstants.SUCCESS_CODE)
                || payResultCache.get(outTradeNo).equals(WxConstants.FAIL_CODE))) {
            return payResultCache.get(outTradeNo);
        }
        HashMap<String, String> data = new HashMap<String, String>();
        //公众账号ID
        data.put("appid", wxConfig.getAppID());
        //商户号
        data.put("mch_id", wxConfig.getMchID());

        //随机字符串
        data.put("nonce_str", NumUtil.getNonceStr());

        //商户订单号
        data.put("out_trade_no", outTradeNo);

        //签名类型
        data.put("sign_type", signType);
        //签名
        data.put("sign", WxUtil.getSignature(data, wxConfig.getKey(), signType));

        String requestXML = WxUtil.mapToXml(data);
        String responseString = HttpsClient.httpsRequestReturnString(WxConstants.PAY_QUERY, WxConstants.METHOD_POST, requestXML);
        Map<String, String> resultMap = WxUtil.processResponseXml(responseString, signType, wxConfig.getKey());
        log.info("微信支付结果查询返回:{}", JSONObject.toJSONString(resultMap));

        List<String> payingList = Arrays.asList("USERPAYING", "ACCEPT");
        if (resultMap.get(WxConstants.RETURN_CODE).equals(WxConstants.SUCCESS_CODE)) {
            if (WxConstants.SUCCESS_CODE.equals(resultMap.get("result_code")) && WxConstants.SUCCESS_CODE.equals(resultMap.get("trade_state"))) {
                payResultCache.put(outTradeNo, WxConstants.SUCCESS_CODE);
                return WxConstants.SUCCESS_CODE;
            } else if (WxConstants.SUCCESS_CODE.equals(resultMap.get("result_code")) && payingList.contains(resultMap.get("trade_state"))) {
                return WxConstants.PROCESSING;
            } else {
                payResultCache.put(outTradeNo, WxConstants.FAIL_CODE);
                return WxConstants.FAIL_CODE;
            }
        }
        // 通信失败认为处理中，等待下次查询
        return WxConstants.PROCESSING;
    }


    public boolean closeOrder(String outTradeNo, String signType) throws Exception {
        HashMap<String, String> data = new HashMap<String, String>();
        //公众账号ID
        data.put("appid", wxConfig.getAppID());
        //商户号
        data.put("mch_id", wxConfig.getMchID());

        //随机字符串
        data.put("nonce_str", NumUtil.getNonceStr());

        //商户订单号
        data.put("out_trade_no", outTradeNo);

        //签名类型
        data.put("sign_type", signType);
        //签名
        data.put("sign", WxUtil.getSignature(data, wxConfig.getKey(), signType));

        String requestXML = WxUtil.mapToXml(data);
        String responseString = HttpsClient.httpsRequestReturnString(WxConstants.CLOSE_ORDER, WxConstants.METHOD_POST, requestXML);
        Map<String, String> resultMap = WxUtil.processResponseXml(responseString, signType, wxConfig.getKey());
        log.info("微信支付结果查询返回:{}", JSONObject.toJSONString(resultMap));

        if (resultMap.get(WxConstants.RETURN_CODE).equals(WxConstants.SUCCESS_CODE)) {
            if (WxConstants.SUCCESS_CODE.equals(resultMap.get("result_code")) || "ORDERCLOSED".equals(resultMap.get("result_code"))) {
                return true;
            } else if ("ORDERPAID".equals(resultMap.get("result_code"))) {
                // 已支付的订单不能关闭，同时缓存支付结果
                payResultCache.put(outTradeNo, WxConstants.SUCCESS_CODE);
                return false;
            }
        }
        return false;
    }

    public boolean processCallBack(InputStream inputStream, String signType) throws Exception {
        String requstXml = HttpsClient.getStreamString(inputStream);
        log.info("processCallBack xml:{}", requstXml);

        Map<String, String> resultMap = WxUtil.processResponseXml(requstXml, signType, wxConfig.getKey());

        if (resultMap.get(WxConstants.RETURN_CODE).equals(WxConstants.SUCCESS_CODE)) {
            String outTradeNo = resultMap.get("out_trade_no");
            //微信支付订单号
            String transactionId = resultMap.get("transaction_id");
            System.out.println("transactionId : " + transactionId);
            //支付完成时间
            SimpleDateFormat payFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            Date payDate = payFormat.parse(resultMap.get("time_end"));
            SimpleDateFormat systemFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            System.out.println("支付时间：" + systemFormat.format(payDate));

            if (WxConstants.SUCCESS_CODE.equals(resultMap.get("result_code"))) {
                payResultCache.put(outTradeNo, WxConstants.SUCCESS_CODE);
            } else {
                payResultCache.put(outTradeNo, WxConstants.FAIL_CODE);
            }
            // 表示通知成功
            return true;
        } else {
            // 表示同时失败
            return false;
        }
    }

    public boolean signValid(Map<String, String> data, String signType) {
        return WxUtil.isSignatureValid(data, wxConfig.getKey(), signType);
    }


    public String getAccessToken(String code) {
        AccessToken accessToken = WxUtil.getAcToken(wxConfig.getAppID(), wxConfig.getAppSecret(), code);
        return accessToken.getToken();
    }

    // 验证服务器配置的token是否一致
    public boolean validateServerToken(String nonce, String timestamp, String signature) {
        log.info("微信请求认证服务端数据:nonce:{}, timestamp:{}, signature:{}", nonce, timestamp, signature);
        String[] str = {wxConfig.getServerToken(), timestamp, nonce};
        // 字典排序
        Arrays.sort(str);
        String bigStr = str[0] + str[1] + str[2];
        // 用sha1做签名
        String sign = SHA1Util.encode(bigStr);
        log.info("微信请求认证服务端数据,服务端产生sign:{}", sign);
        return sign.equals(signature);
    }
}
