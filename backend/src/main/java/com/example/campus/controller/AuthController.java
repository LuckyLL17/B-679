package com.example.campus.controller;

import com.example.campus.entity.User;
import com.example.campus.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private com.example.campus.service.AuthService authService;

    @GetMapping("/me")
    public Map<String, Object> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return null; // Frontend handles null as "not logged in"
        }
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("username", user.getUsername());
        response.put("fullName", user.getFullName());
        response.put("role", user.getRole());
        return response;
    }

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        String fullName = body.get("fullName");
        
        authService.registerUser(username, password, fullName);
        
        return Map.of("success", true, "message", "注册成功");
    }
}
