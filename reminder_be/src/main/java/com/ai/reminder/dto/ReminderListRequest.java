package com.ai.reminder.dto;

import jakarta.validation.constraints.NotBlank;

public record ReminderListRequest(
        @NotBlank String name,
        @NotBlank String color
) {
}