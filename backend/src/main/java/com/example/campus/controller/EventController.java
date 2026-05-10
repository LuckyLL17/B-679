package com.example.campus.controller;

import com.example.campus.entity.Event;
import com.example.campus.entity.User;
import com.example.campus.repository.EventRepository;
import com.example.campus.repository.UserRepository;
import com.example.campus.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/events")
@PreAuthorize("isAuthenticated()")
public class EventController {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public Page<Event> getEvents(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size) {
        return eventRepository.findAll(PageRequest.of(page, size, Sort.by("createdAt").descending()));
    }

    @GetMapping("/{id}")
    public Map<String, Object> getEvent(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        Event event = eventRepository.findById(id).orElseThrow(() -> new RuntimeException("活动不存在"));
        
        Map<String, Object> response = new HashMap<>();
        response.put("id", event.getId());
        response.put("title", event.getTitle());
        response.put("description", event.getDescription());
        response.put("startTime", event.getStartTime());
        response.put("endTime", event.getEndTime());
        response.put("checkinStartTime", event.getCheckinStartTime());
        response.put("checkinEndTime", event.getCheckinEndTime());
        response.put("location", event.getLocation());
        response.put("maxParticipants", event.getMaxParticipants());

        if (userDetails != null) {
            User user = userRepository.findByUsername(userDetails.getUsername()).orElse(null);
            if (user != null) {
                var registration = registrationService.getRegistration(user.getId(), id);
                if (registration.isPresent()) {
                    response.put("isRegistered", true);
                    response.put("hasCheckedIn", registration.get().isCheckinStatus());
                    response.put("checkinTime", registration.get().getCheckinTime());
                } else {
                     response.put("isRegistered", false);
                     response.put("hasCheckedIn", false);
                }
            }
        }
        
        return response;
    }
}
