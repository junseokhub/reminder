package com.ai.reminder.repository;

import com.ai.reminder.domain.ReminderList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReminderListRepository extends JpaRepository<ReminderList, Long> {
}
