package com.ai.reminder.service.ports.in;

import com.ai.reminder.domain.Priority;
import com.ai.reminder.domain.Reminder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ReminderService {

    List<Reminder> findByListId(Long listId);

    Reminder findById(Long id);

    Reminder create(Long listId, String title);

    Reminder update(Long id, String title, String memo, LocalDate dueDate, LocalTime dueTime, Priority priority);

    Reminder toggle(Long id);

    void delete(Long id);
}