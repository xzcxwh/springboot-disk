package com.example.common.config;

// 导入Spring框架的Configuration注解，用于声明当前类是一个配置类
import org.springframework.context.annotation.Configuration;
// 导入Spring Web MVC的InterceptorRegistry和WebMvcConfigurer接口，用于自定义Web MVC配置
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// 导入Javax的Resource注解，用于注入依赖
import javax.annotation.Resource;

// 使用@Configuration注解声明当前类是一个配置类
@Configuration
public class WebConfig implements  WebMvcConfigurer {

    // 使用@Resource注解注入JwtInterceptor类型的bean
    @Resource
    private JwtInterceptor jwtInterceptor;

    // 实现WebMvcConfigurer接口的addInterceptors方法，用于添加自定义拦截器
    // 加自定义拦截器JwtInterceptor，设置拦截规则
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 添加JwtInterceptor拦截器到registry中
        registry.addInterceptor(jwtInterceptor)
                // 设置需要拦截的路径模式为"/**"，表示拦截所有路径
                .addPathPatterns("/**")
                // 排除不需要拦截的路径模式，即以下路径不会被JwtInterceptor拦截
                .excludePathPatterns("/")
                .excludePathPatterns("/login")
                .excludePathPatterns("/register")
                .excludePathPatterns("/files/**")
                .excludePathPatterns("/diskFiles/download/**")
                .excludePathPatterns("/diskFiles/preview/**")
                .excludePathPatterns("/notice/selectAll")
                .excludePathPatterns("/share/selectById/**")
                .excludePathPatterns("/diskFiles/selectShare")
                .excludePathPatterns("/diskFiles/selectFolders");
    }
}