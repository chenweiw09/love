package com.book.xw.web.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.util.ResourceUtils;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private MyInterceptor myInterceptor;
    /**
     * 功能描述：从上到下：1.跨域支持2.配置首页3.配置静态资源访问
     **/

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowedOrigins("*")
                .allowCredentials(true)
                .exposedHeaders(HttpHeaders.SET_COOKIE).maxAge(18000L);
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("index");
        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/src/main/static/**")
                .addResourceLocations(ResourceUtils.CLASSPATH_URL_PREFIX + "/src/main/static/");
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations(ResourceUtils.CLASSPATH_URL_PREFIX + "/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations(ResourceUtils.CLASSPATH_URL_PREFIX + "/META-INF/resources/webjars/");
        registry.addResourceHandler("/image/**").addResourceLocations(ResourceUtils.CLASSPATH_URL_PREFIX + "static/");
        registry.addResourceHandler("/static/**")
                .addResourceLocations(ResourceUtils.CLASSPATH_URL_PREFIX + "static/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(myInterceptor)
                .addPathPatterns("/about-tech")
                .excludePathPatterns("/index");
    }

}


