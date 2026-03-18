package com.ai.reminder.dto;

import com.ai.reminder.domain.Reminder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record ReminderResponse(
        Long id,
        String title,
        String memo,
        LocalDate dueDate,
        LocalTime dueTime,
        String priority,
        boolean completed,
        LocalDateTime completedAt,
        int displayOrder,
        Long listId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ReminderResponse from(Reminder reminder) {
        return new ReminderResponse(
                reminder.getId(),
                reminder.getTitle(),
                reminder.getMemo(),
                reminder.getDueDate(),
                reminder.getDueTime(),
                reminder.getPriority().name(),
                reminder.isCompleted(),
                reminder.getCompletedAt(),
                reminder.getDisplayOrder(),
                reminder.getList().getId(),
                reminder.getCreatedAt(),
                reminder.getUpdatedAt()
        );
    }
}