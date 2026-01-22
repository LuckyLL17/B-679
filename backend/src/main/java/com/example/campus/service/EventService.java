package com.example.campus.service;

import com.example.campus.entity.Event;
import com.example.campus.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private com.example.campus.repository.RegistrationRepository registrationRepository;

    public Page<Event> getAllEvents(Pageable pageable) {
        return eventRepository.findAll(pageable);
    }
    
    public Event getEvent(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("活动不存在"));
    }

    @Transactional
    public Event createEvent(Event event) {
        validateEventTimes(event);
        return eventRepository.save(event);
    }

    @Transactional
    public Event updateEvent(Long id, Event updatedEvent) {
        Event event = getEvent(id);
        
        event.setTitle(updatedEvent.getTitle());
        event.setDescription(updatedEvent.getDescription());
        event.setStartTime(updatedEvent.getStartTime());
        event.setEndTime(updatedEvent.getEndTime());
        event.setCheckinStartTime(updatedEvent.getCheckinStartTime());
        event.setCheckinEndTime(updatedEvent.getCheckinEndTime());
        event.setLocation(updatedEvent.getLocation());
        event.setMaxParticipants(updatedEvent.getMaxParticipants());
        
        validateEventTimes(event);
        return eventRepository.save(event);
    }

    @Transactional
    public void deleteEvent(Long id) {
        registrationRepository.deleteByEventId(id);
        eventRepository.deleteById(id);
    }

    private void validateEventTimes(Event event) {
        if (event.getMaxParticipants() == null) {
            event.setMaxParticipants(0);
        }
        if (event.getMaxParticipants() < 0) {
            throw new IllegalArgumentException("最大人数不能为负数");
        }
        if (event.getStartTime().isAfter(event.getEndTime())) {
            throw new IllegalArgumentException("开始时间必须早于结束时间");
        }
        if (event.getCheckinStartTime().isAfter(event.getCheckinEndTime())) {
            throw new IllegalArgumentException("签到开始时间必须早于签到结束时间");
        }
    }
}
