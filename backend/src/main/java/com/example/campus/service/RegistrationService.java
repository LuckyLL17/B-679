package com.example.campus.service;

import com.example.campus.entity.Event;
import com.example.campus.entity.Registration;
import com.example.campus.entity.User;
import com.example.campus.repository.EventRepository;
import com.example.campus.repository.RegistrationRepository;
import com.example.campus.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class RegistrationService {

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

	public void register(Long userId, Long eventId) {
		Event event = eventRepository.findById(eventId)
				.orElseThrow(() -> new RuntimeException("活动不存在"));

        if (java.time.LocalDateTime.now().isAfter(event.getEndTime())) {
            throw new RuntimeException("活动已结束，无法报名");
        }

		if (registrationRepository.findByUserIdAndEventId(userId, eventId).isPresent()) {
			throw new RuntimeException("您已报名该活动");
		}

		// Optional: Check capacity
		Integer maxParticipants = event.getMaxParticipants();
		if (maxParticipants != null && maxParticipants > 0) {
			long currentCount = registrationRepository.findByEventId(eventId).size(); // Simplified count, preferably count query
			if (currentCount >= maxParticipants) {
				throw new RuntimeException("报名人数已满");
			}
		}

        User user = userRepository.getReferenceById(userId);
        
        Registration registration = new Registration();
        registration.setUser(user);
        registration.setEvent(event);
        registration.setStatus(Registration.Status.REGISTERED);
        
        registrationRepository.save(registration);
    }

    @Transactional
    public void cancelRegistration(Long userId, Long eventId) {
        Registration registration = registrationRepository.findByUserIdAndEventId(userId, eventId)
                .orElseThrow(() -> new RuntimeException("未找到报名记录"));
        
        registrationRepository.delete(registration);
    }

    public boolean isRegistered(Long userId, Long eventId) {
        return registrationRepository.findByUserIdAndEventId(userId, eventId).isPresent();
    }
    
    public Optional<Registration> getRegistration(Long userId, Long eventId) {
        return registrationRepository.findByUserIdAndEventId(userId, eventId);
    }

    @Transactional
    public void checkIn(Long userId, Long eventId) {
        Registration registration = registrationRepository.findByUserIdAndEventId(userId, eventId)
                .orElseThrow(() -> new RuntimeException("您未报名该活动"));

        Event event = registration.getEvent();
        java.time.LocalDateTime now = java.time.LocalDateTime.now();

        if (now.isBefore(event.getCheckinStartTime())) {
            throw new RuntimeException("签到尚未开始");
        }
        if (now.isAfter(event.getCheckinEndTime())) {
            throw new RuntimeException("签到已截止");
        }
        
        if (registration.isCheckinStatus()) {
             throw new RuntimeException("您已签到，无需重复操作");
        }

        registration.setCheckinStatus(true);
        registration.setCheckinTime(now);
        registrationRepository.save(registration);
    }

    public Iterable<Registration> getRegistrationsForEvent(Long eventId) {
        return registrationRepository.findByEventId(eventId);
    }
}
