package com.ai.reminder.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reminder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String memo;

    private LocalDate dueDate;

    private LocalTime dueTime;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    private boolean completed;

    private LocalDateTime completedAt;

    private int displayOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "list_id", nullable = false)
    private ReminderList list;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Builder
    public Reminder(String title, ReminderList list) {
        this.title = title;
        this.list = list;
        this.completed = false;
        this.priority = Priority.NONE;
        this.displayOrder = 0;
        var now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    public void update(String title, String memo, LocalDate dueDate, LocalTime dueTime, Priority priority) {
        this.title = title;
        this.memo = memo;
        this.dueDate = dueDate;
        this.dueTime = dueTime;
        this.priority = priority;
        this.updatedAt = LocalDateTime.now();
    }

    public void toggleCompleted() {
        this.completed = !this.completed;
        this.completedAt = this.completed ? LocalDateTime.now() : null;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
        this.updatedAt = LocalDateTime.now();
    }
}