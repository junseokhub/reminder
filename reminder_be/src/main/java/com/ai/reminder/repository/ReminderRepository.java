package com.ai.reminder.repository;

import com.ai.reminder.domain.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReminderRepository extends JpaRepository<Reminder, Long> {

    List<Reminder> findByListIdOrderByDisplayOrderAscCreatedAtAsc(Long listId);

    long countByListIdAndCompletedFalse(Long listId);
}