package com.ai.reminder.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ReminderTest {

    private ReminderList sampleList() {
        return ReminderList.builder()
                .name("미리 알림")
                .color("#007AFF")
                .isDefault(true)
                .build();
    }

    @Test
    @DisplayName("Builder로 Reminder를 생성할 수 있다")
    void createWithBuilder() {
        var list = sampleList();
        var reminder = Reminder.builder()
                .title("장보기")
                .list(list)
                .build();

        assertThat(reminder.getTitle()).isEqualTo("장보기");
        assertThat(reminder.getList()).isEqualTo(list);
        assertThat(reminder.isCompleted()).isFalse();
        assertThat(reminder.getPriority()).isEqualTo(Priority.NONE);
        assertThat(reminder.getMemo()).isNull();
        assertThat(reminder.getDueDate()).isNull();
    }

    @Test
    @DisplayName("생성 시 createdAt과 updatedAt이 자동 설정된다")
    void constructorSetsTimestamps() {
        var before = LocalDateTime.now();
        var reminder = Reminder.builder()
                .title("장보기")
                .list(sampleList())
                .build();
        var after = LocalDateTime.now();

        assertThat(reminder.getCreatedAt()).isBetween(before, after);
        assertThat(reminder.getCreatedAt()).isEqualTo(reminder.getUpdatedAt());
    }

    @Test
    @DisplayName("update로 모든 상세 필드를 변경할 수 있다")
    void update() {
        var reminder = Reminder.builder()
                .title("장보기")
                .list(sampleList())
                .build();

        reminder.update("우유 사기", "서울우유", LocalDate.of(2026, 3, 10), LocalTime.of(14, 0), Priority.HIGH);

        assertThat(reminder.getTitle()).isEqualTo("우유 사기");
        assertThat(reminder.getMemo()).isEqualTo("서울우유");
        assertThat(reminder.getDueDate()).isEqualTo(LocalDate.of(2026, 3, 10));
        assertThat(reminder.getDueTime()).isEqualTo(LocalTime.of(14, 0));
        assertThat(reminder.getPriority()).isEqualTo(Priority.HIGH);
    }

    @Test
    @DisplayName("toggleCompleted로 완료 상태를 토글할 수 있다")
    void toggleCompleted() {
        var reminder = Reminder.builder()
                .title("장보기")
                .list(sampleList())
                .build();

        assertThat(reminder.isCompleted()).isFalse();
        assertThat(reminder.getCompletedAt()).isNull();

        reminder.toggleCompleted();
        assertThat(reminder.isCompleted()).isTrue();
        assertThat(reminder.getCompletedAt()).isNotNull();

        reminder.toggleCompleted();
        assertThat(reminder.isCompleted()).isFalse();
        assertThat(reminder.getCompletedAt()).isNull();
    }

    @Test
    @DisplayName("update와 toggle 시 updatedAt이 갱신된다")
    void updatedAtRefreshes() throws InterruptedException {
        var reminder = Reminder.builder()
                .title("장보기")
                .list(sampleList())
                .build();
        var original = reminder.getUpdatedAt();

        Thread.sleep(10);
        reminder.update("우유 사기", null, null, null, Priority.NONE);
        assertThat(reminder.getUpdatedAt()).isAfter(original);

        var afterUpdate = reminder.getUpdatedAt();
        Thread.sleep(10);
        reminder.toggleCompleted();
        assertThat(reminder.getUpdatedAt()).isAfter(afterUpdate);
    }

    @Test
    @DisplayName("displayOrder를 변경할 수 있다")
    void updateDisplayOrder() {
        var reminder = Reminder.builder()
                .title("장보기")
                .list(sampleList())
                .build();

        assertThat(reminder.getDisplayOrder()).isEqualTo(0);

        reminder.updateDisplayOrder(5);
        assertThat(reminder.getDisplayOrder()).isEqualTo(5);
    }
}