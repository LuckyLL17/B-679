package com.example.campus.filter;

import com.example.campus.entity.User;
import com.example.campus.exception.AccountDisabledException;
import com.example.campus.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AccountStatusFilter extends OncePerRequestFilter {

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated() 
                && !"anonymousUser".equals(authentication.getPrincipal())) {
            String username = authentication.getName();
            userRepository.findByUsername(username).ifPresent(user -> {
                if (user.getStatus() == User.Status.DISABLED) {
                    throw new AccountDisabledException("账号已被禁用");
                }
                if (user.getStatus() == User.Status.EXPIRED) {
                    throw new AccountDisabledException("账号已过期");
                }
            });
        }
        
        filterChain.doFilter(request, response);
    }
}
