package com.example.campus.controller;

import com.example.campus.entity.User;
import com.example.campus.repository.UserRepository;
import com.example.campus.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/events/{eventId}")
public class RegistrationController {

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/registrations")
    public Map<String, Object> register(@PathVariable Long eventId, 
                                        @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        registrationService.register(userId, eventId);
        return Map.of("success", true, "message", "报名成功");
    }

    @DeleteMapping("/registrations")
    public Map<String, Object> cancelRegistration(@PathVariable Long eventId,
                                                  @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        registrationService.cancelRegistration(userId, eventId);
        return Map.of("success", true, "message", "已取消报名");
    }

    @PostMapping("/checkin")
    public Map<String, Object> checkIn(@PathVariable Long eventId,
                                       @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        registrationService.checkIn(userId, eventId);
        return Map.of("success", true, "message", "签到成功", "checkinTime", java.time.LocalDateTime.now());
    }

    private Long getUserId(UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("用户不存在"))
                .getId();
    }
}
