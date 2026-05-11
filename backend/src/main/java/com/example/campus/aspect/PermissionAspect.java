package com.example.campus.aspect;

import com.example.campus.common.RequiresPermission;
import com.example.campus.entity.User;
import com.example.campus.exception.PermissionDeniedException;
import com.example.campus.repository.UserRepository;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;

@Aspect
@Component
public class PermissionAspect {

    @Autowired
    private UserRepository userRepository;

    @Around("@annotation(com.example.campus.common.RequiresPermission) || @within(com.example.campus.common.RequiresPermission)")
    public Object checkPermission(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        
        RequiresPermission methodAnnotation = method.getAnnotation(RequiresPermission.class);
        RequiresPermission classAnnotation = method.getDeclaringClass().getAnnotation(RequiresPermission.class);
        
        RequiresPermission permissionAnnotation = methodAnnotation != null ? methodAnnotation : classAnnotation;
        
        if (permissionAnnotation != null && permissionAnnotation.value().length > 0) {
            User.Role[] requiredRoles = permissionAnnotation.value();
            User currentUser = getCurrentUser();
            
            if (currentUser == null) {
                throw new PermissionDeniedException("用户未登录");
            }
            
            boolean hasPermission = Arrays.stream(requiredRoles)
                    .anyMatch(role -> role == currentUser.getRole());
            
            if (!hasPermission) {
                throw new PermissionDeniedException("没有权限访问该接口");
            }
        }
        
        return joinPoint.proceed();
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }
        String username = authentication.getName();
        return userRepository.findByUsername(username).orElse(null);
    }
}
