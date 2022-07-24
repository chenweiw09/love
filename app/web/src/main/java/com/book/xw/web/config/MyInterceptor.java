package com.book.xw.web.config;

import com.book.xw.common.util.utils.GuavaCache;
import com.book.xw.web.util.InetAddressUtil;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Log
@Component
public class MyInterceptor implements HandlerInterceptor {

    @Autowired
    private GuavaCache guavaCache;

    private static final String REQ_KEY = "WEB_REQ";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        if (session == null) {
            session = request.getSession();
            String remoteIp= InetAddressUtil.getIpAddr(request);
            session.setAttribute("ip", remoteIp);
            Object oldValue = guavaCache.getCache(REQ_KEY);
            log.info("访问量统计计数---------------ip="+remoteIp+",count 1");
            if (oldValue != null) {
                guavaCache.setCache(REQ_KEY, (int) oldValue + 1);
            } else {
                guavaCache.setCache(REQ_KEY, 1);
            }

        }
        return true;
    }

}
