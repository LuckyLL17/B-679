package com.example.campus.repository;

import com.example.campus.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EventRepository extends JpaRepository<Event, Long> {
    Page<Event> findByTitleContaining(String keyword, Pageable pageable);
}
