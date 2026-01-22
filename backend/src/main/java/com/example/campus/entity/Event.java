package com.example.campus.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false)
    private LocalDateTime checkinStartTime;

    @Column(nullable = false)
    private LocalDateTime checkinEndTime;

    private String location;

    private Integer maxParticipants = 0; // 0 means unlimited

    @CreationTimestamp
    private LocalDateTime createdAt;
}
