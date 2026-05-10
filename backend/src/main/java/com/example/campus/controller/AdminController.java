package com.example.campus.controller;

import com.example.campus.entity.Event;
import com.example.campus.entity.User;
import com.example.campus.repository.UserRepository;
import com.example.campus.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventService eventService;

    @Autowired
    private com.example.campus.service.RegistrationService registrationService;

    @GetMapping("/events")
    public Page<Event> getEvents(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size) {
        return eventService.getAllEvents(PageRequest.of(page, size, Sort.by("createdAt").descending()));
    }

    @GetMapping("/events/{id}")
    public Event getEvent(@PathVariable Long id) {
        return eventService.getEvent(id);
    }

    @PostMapping("/events")
    public Event createEvent(@RequestBody Event event) {
        return eventService.createEvent(event);
    }

    @PutMapping("/events/{id}")
    public Event updateEvent(@PathVariable Long id, @RequestBody Event event) {
        return eventService.updateEvent(id, event);
    }

    @DeleteMapping("/events/{id}")
    public Map<String, Boolean> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return Map.of("success", true);
    }

    @GetMapping("/events/{eventId}/registrations")
    public Iterable<java.util.Map<String, Object>> getEventRegistrations(@PathVariable Long eventId) {
        java.util.List<java.util.Map<String, Object>> result = new java.util.ArrayList<>();
        
        for (com.example.campus.entity.Registration r : registrationService.getRegistrationsForEvent(eventId)) {
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", r.getId());
            map.put("userId", r.getUser().getId());
            map.put("fullName", r.getUser().getFullName());
            map.put("status", r.getStatus());
            map.put("checkinStatus", r.isCheckinStatus());
            map.put("checkinTime", r.getCheckinTime());
            map.put("registrationTime", r.getRegistrationTime());
            result.add(map);
        }
        return result;
    }

    @GetMapping("/users")
    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

    @PutMapping("/users/{userId}/status")
    public Map<String, Object> updateUserStatus(@PathVariable Long userId, @RequestBody Map<String, String> body) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("用户不存在"));
        String statusStr = body.get("status");
        user.setStatus(User.Status.valueOf(statusStr.toUpperCase()));
        userRepository.save(user);
        return Map.of("success", true, "message", "状态更新成功");
    }
}
