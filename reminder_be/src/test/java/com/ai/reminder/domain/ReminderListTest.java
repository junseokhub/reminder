package com.ai.reminder.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ReminderListTest {

    @Test
    @DisplayName("Builder로 ReminderList를 생성할 수 있다")
    void createReminderList() {
        ReminderList list = ReminderList.builder()
                .name("미리 알림")
                .color("#007AFF")
                .isDefault(true)
                .build();

        assertThat(list.getName()).isEqualTo("미리 알림");
        assertThat(list.getColor()).isEqualTo("#007AFF");
        assertThat(list.isDefault()).isTrue();
    }


    @Test
    @DisplayName("update로 이름과 색상을 변경할 수 있다")
    void update() {
        ReminderList list = ReminderList.builder()
                .name("업무")
                .color("#FF3830")
                .isDefault(false)
                .build();

        list.update("개인", "#34C759");

        assertThat(list.getName()).isEqualTo("개인");
        assertThat(list.getColor()).isEqualTo("#34C759");
    }

    @Test
    @DisplayName("update 시 updatedAt이 갱신되고 createdAt은 유지된다")
    void updateRefreshUpdatedAt() throws InterruptedException {
        ReminderList list = ReminderList.builder()
                .name("업무")
                .color("#FF3830")
                .isDefault(false)
                .build();

        var createdAt = list.getCreatedAt();
        var originalUpdatedAt = list.getUpdatedAt();

        Thread.sleep(10);

        list.update("개인", "#34C759");

        assertThat(list.getUpdatedAt()).isEqualTo(originalUpdatedAt);
        assertThat(list.getCreatedAt()).isEqualTo(createdAt);
    }
}