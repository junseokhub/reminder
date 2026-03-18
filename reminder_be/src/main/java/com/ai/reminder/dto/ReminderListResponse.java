package com.ai.reminder.dto;

import com.ai.reminder.domain.ReminderList;

import java.time.LocalDateTime;

public record ReminderListResponse(
        Long id,
        String name,
        String color,
        boolean isDefault,
        long reminderCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ReminderListResponse from(ReminderList list, long reminderCount) {
        return new ReminderListResponse(
                list.getId(),
                list.getName(),
                list.getColor(),
                list.isDefault(),
                reminderCount,
                list.getCreatedAt(),
                list.getUpdatedAt()
        );
    }
}