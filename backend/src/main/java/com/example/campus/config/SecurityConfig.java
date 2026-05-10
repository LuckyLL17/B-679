package com.example.campus.config;

import com.example.campus.common.ApiResponse;
import com.example.campus.filter.AccountStatusFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import java.util.Map;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private AccountStatusFilter accountStatusFilter;

    @Autowired
    private SessionExpiredHandler sessionExpiredHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .invalidSessionStrategy(sessionExpiredHandler)
                .maximumSessions(1)
                .expiredSessionStrategy(sessionExpiredHandler)
                .and()
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/login", "/api/auth/register", "/api/auth/me").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .addFilterBefore(accountStatusFilter, UsernamePasswordAuthenticationFilter.class)
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
                    resp.setStatus(200);
                    resp.setContentType("application/json;charset=UTF-8");
                    ApiResponse<Object> response = ApiResponse.error(401, "用户名或密码错误");
                    resp.getWriter().write(objectMapper.writeValueAsString(response));
                })
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/api/auth/logout")
                .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler())
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .permitAll()
            );
            
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
