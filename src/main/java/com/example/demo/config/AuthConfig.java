package com.example.demo.config;

import com.example.demo.Service.user.AccountService;
import com.example.demo.interceptor.AuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class AuthConfig implements WebMvcConfigurer {
    private final AuthInterceptor authInterceptor;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/auth/**",
                        "/account/sign-up",
                        "/account/forgot-password",
                        "/images/**",
                        "/css/**",
                        "/js/**",
                        "/error",
                        "/",
                        "/home",
                        "/product/**"
                );
    }
}
