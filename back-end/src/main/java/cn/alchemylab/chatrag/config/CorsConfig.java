package cn.alchemylab.chatrag.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")               // 允许所有路径
//                .allowedOrigins("http://localhost:5173") // 允许来自这个端口的请求（前端地址）
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE") // 允许的请求方式
                .allowedHeaders("*")                    // 允许的请求头
                .allowCredentials(false);                // 是否允许发送 cookie
    }
}

