package com.example.campus.component;

import com.example.campus.entity.Event;
import com.example.campus.entity.Registration;
import com.example.campus.entity.User;
import com.example.campus.repository.EventRepository;
import com.example.campus.repository.RegistrationRepository;
import com.example.campus.repository.UserRepository;
import com.example.campus.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Profile("dev") // 仅在 dev 环境执行，生产环境不会加载此 Bean
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RegistrationRepository registrationRepository;

    @Override
    public void run(String... args) throws Exception {
        if (eventRepository.count() > 0) {
            System.out.println("Data already exists, skipping seed.");
            return;
        }

        System.out.println("Seeding test data...");

        // 1. Create Users
        try {
            authService.registerUser("student1", "123456", "张三");
            authService.registerUser("student2", "123456", "李四");
        } catch (Exception e) {
            System.out.println("User creation skipped: " + e.getMessage());
        }

        User u1 = userRepository.findByUsername("student1").orElseThrow();
        User u2 = userRepository.findByUsername("student2").orElseThrow();

        // 2. Create Events
        LocalDateTime now = LocalDateTime.now();

        // Event 1: Active with Check-in OPEN (Starts 1hr ago, Ends 3hr later, Check-in window surrounds NOW)
        Event activeEvent = new Event();
        activeEvent.setTitle("秋季校园编程马拉松");
        activeEvent.setDescription("年度最盛大的编程比赛，欢迎全校师生参加！现场提供披萨和饮料。");
        activeEvent.setLocation("图书馆报告厅");
        activeEvent.setStartTime(now.minusHours(1));
        activeEvent.setEndTime(now.plusHours(3));
        // Check-in window: 30 mins ago to 1 hour later
        activeEvent.setCheckinStartTime(now.minusMinutes(30));
        activeEvent.setCheckinEndTime(now.plusHours(1));
        activeEvent.setMaxParticipants(100);
        eventRepository.save(activeEvent);

        // Event 2: Upcoming (Starts tomorrow)
        Event upcomingEvent = new Event();
        upcomingEvent.setTitle("人工智能前沿讲座");
        upcomingEvent.setDescription("特邀知名AI专家分享深度学习在医疗领域的应用。");
        upcomingEvent.setLocation("第二教学楼 303");
        upcomingEvent.setStartTime(now.plusDays(1).withHour(14).withMinute(0));
        upcomingEvent.setEndTime(now.plusDays(1).withHour(16).withMinute(0));
        upcomingEvent.setCheckinStartTime(now.plusDays(1).withHour(13).withMinute(30));
        upcomingEvent.setCheckinEndTime(now.plusDays(1).withHour(14).withMinute(30));
        eventRepository.save(upcomingEvent);

        // Event 3: Ended
        Event endedEvent = new Event();
        endedEvent.setTitle("上学期社团招新");
        endedEvent.setDescription("百团大战，精彩纷呈！");
        endedEvent.setLocation("操场");
        endedEvent.setStartTime(now.minusDays(30));
        endedEvent.setEndTime(now.minusDays(30).plusHours(4));
        endedEvent.setCheckinStartTime(now.minusDays(30));
        endedEvent.setCheckinEndTime(now.minusDays(30).plusHours(4));
        eventRepository.save(endedEvent);

        // Event 4: 专门用于签到测试 - 超长签到窗口 (当前时间前1小时 ~ 后24小时)
        Event checkinTestEvent = new Event();
        checkinTestEvent.setTitle("【测试】签到功能验证活动");
        checkinTestEvent.setDescription("此活动专用于验证签到流程，签到窗口超长，方便随时测试。");
        checkinTestEvent.setLocation("线上");
        checkinTestEvent.setStartTime(now.minusHours(2));
        checkinTestEvent.setEndTime(now.plusDays(7));
        checkinTestEvent.setCheckinStartTime(now.minusHours(1)); // 从1小时前开始
        checkinTestEvent.setCheckinEndTime(now.plusDays(1));     // 到明天才结束
        checkinTestEvent.setMaxParticipants(0); // 不限人数
        eventRepository.save(checkinTestEvent);

        // 3. Create Registrations
        
        // 张三: Registered for Active Event & Checked In
        Registration r1 = new Registration();
        r1.setUser(u1);
        r1.setEvent(activeEvent);
        r1.setCheckinStatus(true);
        r1.setCheckinTime(now); // Checks in "now"
        registrationRepository.save(r1);

        // 李四: Registered for Active Event, NOT Checked In
        Registration r2 = new Registration();
        r2.setUser(u2);
        r2.setEvent(activeEvent);
        r2.setCheckinStatus(false);
        registrationRepository.save(r2);

        // 张三: Registered for Upcoming Event
        Registration r3 = new Registration();
        r3.setUser(u1);
        r3.setEvent(upcomingEvent);
        registrationRepository.save(r3);

        System.out.println("Test data seeded successfully!");
    }
}
