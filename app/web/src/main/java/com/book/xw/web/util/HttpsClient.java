package com.book.xw.web.util;

import com.book.xw.web.constant.WxConstants;
import lombok.SneakyThrows;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;

import static com.book.xw.web.constant.WxConstants.CONNECTION_TIMEOUT;

/**
 * HttpsClient类
 */
public class HttpsClient {

    /**
     * 发起https请求
     *
     * @param requestUrl    请求地址
     * @param requestMethod 请求方式（Get或者post）
     * @param postData      提交数据
     * @return String
     */
    public static String httpsRequestReturnString(String requestUrl, String requestMethod, String postData) {
        String response;
        HttpsURLConnection httpsUrlConnection = null;
        try {
            //创建https请求证书
            TrustManager[] tm = {new MyX509TrustManager()};
            //创建SSLContext管理器对像，使用我们指定的信任管理器初始化
            SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
            sslContext.init(null, tm, new java.security.SecureRandom());
            SSLSocketFactory ssf = sslContext.getSocketFactory();

            // 创建URL对象
            URL url = new URL(requestUrl);
            // 创建HttpsURLConnection对象，并设置其SSLSocketFactory对象
            httpsUrlConnection = (HttpsURLConnection) url.openConnection();
            //设置ssl证书
            httpsUrlConnection.setSSLSocketFactory(ssf);

            //设置header信息
            httpsUrlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            //设置User-Agent信息
            httpsUrlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.146 Safari/537.36");
            //设置可接受信息
            httpsUrlConnection.setDoOutput(true);
            //设置可输入信息
            httpsUrlConnection.setDoInput(true);
            //不使用缓存
            httpsUrlConnection.setUseCaches(false);
            //设置请求方式（GET/POST）
            httpsUrlConnection.setRequestMethod(requestMethod);
            httpsUrlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
            httpsUrlConnection.setReadTimeout(WxConstants.READ_TIMEOUT);
            //设置编码
            httpsUrlConnection.setRequestProperty("Charset", WxConstants.DEFAULT_CHARSET);

            //判断是否需要提交数据
            if (WxConstants.METHOD_POST.equals(requestMethod)) {
                //讲参数转换为字节提交
                byte[] bytes = postData.getBytes(WxConstants.DEFAULT_CHARSET);
                //设置头信息
                httpsUrlConnection.setRequestProperty("Content-Length", Integer.toString(bytes.length));
                //开始连接
                httpsUrlConnection.connect();
                //防止中文乱码
                OutputStream outputStream = httpsUrlConnection.getOutputStream();
                outputStream.write(postData.getBytes(WxConstants.DEFAULT_CHARSET));
                outputStream.flush();
                outputStream.close();
            } else {
                //开始连接
                httpsUrlConnection.connect();
            }
            response = getStreamString(httpsUrlConnection.getInputStream());
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (httpsUrlConnection != null) {
                // 关闭连接
                httpsUrlConnection.disconnect();
            }
        }
        return response;
    }


    /**
     * 输入流转化为字符串
     * @param inputStream 流
     * @return String 字符串
     * @throws Exception
     */
    @SneakyThrows
    public static String getStreamString(InputStream inputStream){
        StringBuffer buffer=new StringBuffer();
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        try{
            inputStreamReader=new InputStreamReader(inputStream, WxConstants.DEFAULT_CHARSET);
            bufferedReader=new BufferedReader(inputStreamReader);
            String line;
            while((line=bufferedReader.readLine())!=null){
                buffer.append(line);
            }
        }catch(Exception e){
            throw new RuntimeException(e);
        }finally {
            if(bufferedReader != null){
                bufferedReader.close();
            }
            if(inputStreamReader != null){
                inputStreamReader.close();
            }
            if(inputStream != null){
                inputStream.close();
            }
        }
        return buffer.toString();
    }


}
