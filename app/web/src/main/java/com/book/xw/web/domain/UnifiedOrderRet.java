package com.book.xw.web.domain;

import lombok.Data;

import java.io.Serializable;

// 统一支付返回数据结构
@Data
public class UnifiedOrderRet implements Serializable {

    private String return_code;

    private String return_msg;

    private String appid;

    private String mch_id;

    private String nonce_str;

    private String sign;

    // 有SUCCESS/FAIL
    private String result_code;

    private String err_code;

    private String err_code_des;

    //return_code 和result_code都为SUCCESS的时候有返回

    private String trade_type;

    // 微信生成的预支付会话标识，用于后续接口调用中使用
    private String prepay_id;

    // trade_type=NATIVE时有返回，此url用于生成支付二维码，然后提供给用户进行扫码支付。
    private String code_url;

}
