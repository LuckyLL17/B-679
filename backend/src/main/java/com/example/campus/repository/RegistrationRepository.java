package com.example.campus.repository;

import com.example.campus.entity.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    Optional<Registration> findByUserIdAndEventId(Long userId, Long eventId);
    List<Registration> findByEventId(Long eventId);
    void deleteByEventId(Long eventId);
}
