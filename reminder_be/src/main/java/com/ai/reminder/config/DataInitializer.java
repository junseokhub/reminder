package com.ai.reminder.config;

import com.ai.reminder.domain.ReminderList;
import com.ai.reminder.repository.ReminderListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final ReminderListRepository repository;

    @Override
    public void run(ApplicationArguments args) {
        if (repository.count() == 0) {
            repository.save(ReminderList.builder()
                    .name("미리 알림")
                    .color("#007AFF")
                    .isDefault(true)
                    .build());
        }
    }
}