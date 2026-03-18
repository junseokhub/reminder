package com.ai.reminder.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record ReminderRequest(
        @NotNull Long listId,
        @NotBlank String title,
        String memo,
        LocalDate dueDate,
        LocalTime dueTime,
        String priority
) {
}