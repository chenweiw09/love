package com.book.xw.core.model.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 微信通用接口凭证
 */
@Data
@NoArgsConstructor
public class AccessToken {
	// 获取到的凭证
    private String token;
    // 凭证有效时间，单位：秒
    private int expiresIn;
    //错误码
    private int errcode;
    //错误信息
    private String  errmsg;
	//用户刷新token
    private String refreshToken;
	//用户oppenId
    private String openId;//
	//用户授权的作用域
	private String scope;//

	public AccessToken(String token, int expiresIn, int errcode, String errmsg) {
		super();
		this.token = token;
		this.expiresIn = expiresIn;
		this.errcode = errcode;
		this.errmsg = errmsg;
	}

}
