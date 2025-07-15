package com.example.new2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CrosConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                    .allowedOrigins("http://192.168.22.63:30070")
             //       .allowedOrigins("*")
                    //.allowedOriginPatterns("*")  // 와일드카드 패턴 허용
               //     .allowedOriginPatterns("http://192.168.22.62:30070", "http://192.168.22.61:30070", "http://192.168.22.63:30070")
                    .allowedMethods("*")
                    .allowedHeaders("*");
                    //.allowCredentials(false);  
                   // .allowCredentials(true);                
            }
        };
    }
}
