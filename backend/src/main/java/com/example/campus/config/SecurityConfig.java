package com.example.campus.config;

import com.example.campus.common.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import java.util.Map;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // 简化开发，生产环境需配置CSRF
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/login", "/api/auth/register").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginProcessingUrl("/api/auth/login")
                .successHandler((req, resp, auth) -> {
                    resp.setContentType("application/json;charset=UTF-8");
                    ApiResponse<Map<String, String>> response = ApiResponse.success(
                        "登录成功",
                        Map.of("username", auth.getName())
                    );
                    resp.getWriter().write(objectMapper.writeValueAsString(response));
                }) 
                .failureHandler((req, resp, ex) -> {
                    resp.setStatus(200); // 统一返回200
                    resp.setContentType("application/json;charset=UTF-8");
                    ApiResponse<Object> response = ApiResponse.error(401, "用户名或密码错误");
                    resp.getWriter().write(objectMapper.writeValueAsString(response));
                })
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/api/auth/logout")
                .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler()) // 登出返回200
                .permitAll()
            );
            
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
