package com.book.xw.web.controller;


import com.alibaba.fastjson.JSONObject;
import com.book.wx.shared.service.WxMenuService;
import com.book.wx.shared.util.QRCodeUtil;
import com.book.xw.common.util.constants.WxConstants;
import com.book.xw.common.util.utils.NumUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;


@Slf4j
@RequestMapping("/wx_pay")
@Controller
public class WxPayController {

    @Autowired
    private WxMenuService wxMenuService;

    /**
     * 二维码首页
     */
    @GetMapping("/")
    public String wxPayList(Model model) {
        //商户订单号
        model.addAttribute("outTradeNo", NumUtil.mchOrderNo());
        return "wxPayList";
    }


    @PostMapping("/trade_no")
    @ResponseBody
    public String getOutTradeNo(Model model) {
        return NumUtil.mchOrderNo();
    }


    /**
     * 统一下单-生成二维码
     */
    @GetMapping("/pay_url")
    public void payUrl(HttpServletRequest request, HttpServletResponse response,
                       @RequestParam(value = "totalFee") Double totalFee,
                       @RequestParam(value = "outTradeNo") String outTradeNo) throws Exception {
        String payUrl = wxMenuService.wxPayUrl(totalFee, outTradeNo, WxConstants.SING_MD5);
        log.info("payUrl:{}",payUrl);
        writerPayImage(response, payUrl);
    }


    /**
     * 统一下单-通知链接
     */
    @RequestMapping(value = {"/callback"})
    public void wxCallBack(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //商户订单号
        String outTradeNo = null;
        String xmlContent = "<xml>" +
                "<return_code><![CDATA[FAIL]]></return_code>" +
                "<return_msg><![CDATA[签名失败]]></return_msg>" +
                "</xml>";
        try {
            boolean notifySuccess = wxMenuService.processCallBack(request.getInputStream(), WxConstants.SING_MD5);
            if(notifySuccess){
                xmlContent = "<xml>" +
                        "<return_code><![CDATA[SUCCESS]]></return_code>" +
                        "<return_msg><![CDATA[OK]]></return_msg>" +
                        "</xml>";
            }
            responsePrint(response, xmlContent);
        } catch (Exception e) {
            log.error("回调结果通知失败,等待重试", e);
            responsePrint(response, xmlContent);
        }
    }

    /**
     * 定时器查询是否已支付
     */
    @GetMapping("/pay_status")
    @ResponseBody
    public String payStatus(@RequestParam(value = "outTradeNo") String outTradeNo) throws Exception {
        JSONObject responseObject = new JSONObject();
        String status = wxMenuService.queryOrder(outTradeNo, WxConstants.SING_MD5);
        responseObject.put("status", status);
        return responseObject.toJSONString();
    }

    // 用来认证服务器的
    @RequestMapping("/token_auth")
    public void authServer(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String signature = request.getParameter("signature");
        // 随机字符串
        String echostr = request.getParameter("echostr");
        // 时间戳
        String timestamp = request.getParameter("timestamp");
        // 随机数
        String nonce = request.getParameter("nonce");

        boolean check = wxMenuService.validateServerToken(nonce, timestamp, signature);
        if(check){
            response.getWriter().print(echostr);
        }else{
            response.getWriter().println("");
        }
    }

//    //首次进入
//    @RequestMapping(value = "/redirect", method = RequestMethod.GET)
//    public String getCode(String appid, String redirect_uri, String scope) {
//        try {
//            appid = "wxab8acb865bb1637e";
//            scope = "snsapi_base";
//            String url = "http://www.cdyjlc.com:8080/oauth";//进入回调地址
//            String redirectURL = URLEncoder.encode(url, "UTF-8");
//            String requestUrl = "redirect:" + WxConstants.GET_CODE.replace("APPID", appid).replace("REDIRECT_URI", redirectURL).replace("SCOPE", scope);
//            return requestUrl;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return "";
//    }

//    //回调地址访问
//    @RequestMapping("/oauth")
//    public void getToken(HttpServletRequest request) {
//        String appid = "wxab8acb865bb1637e"; //开发ID
//        String appsecret = "86ae4a77893342f7568947e243c84d9aa"; //开发秘钥
//        String code = request.getParameter("code"); //获取code
//        AccessToken token = WeixinUtil.getAcToken(appid, appsecret, code);
//        token.getToken();//获取token
//        token.getOpenId();// 获取用户oppenId
//        //跳转前端页面
//    }

    private static void writerPayImage(HttpServletResponse response, String contents) throws Exception {
        ServletOutputStream out = response.getOutputStream();
        try {
            BufferedImage qrImage = QRCodeUtil.createImage(contents, null, "扫描支付");
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

    private static void responsePrint(HttpServletResponse response, String content) throws Exception {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/xml");
        response.getWriter().print(content);
        response.getWriter().flush();
        response.getWriter().close();
    }

}
