package com.ai.reminder.service;

import com.ai.reminder.domain.Priority;
import com.ai.reminder.domain.ReminderList;
import com.ai.reminder.repository.ReminderListRepository;
import com.ai.reminder.repository.ReminderRepository;
import com.ai.reminder.service.ports.in.ReminderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.NoSuchElementException;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class ReminderServiceTest {

    @Autowired
    private ReminderService service;

    @Autowired
    private ReminderRepository reminderRepository;

    @Autowired
    private ReminderListRepository listRepository;

    private ReminderList list;

    @BeforeEach
    void setUp() {
        reminderRepository.deleteAll();
        listRepository.deleteAll();

        list = listRepository.save(ReminderList.builder()
                .name("미리 알림")
                .color("#007AFF")
                .isDefault(true)
                .build());
    }

    @Test
    @DisplayName("목록별 리마인더를 조회할 수 있다")
    void findByListId() {
        service.create(list.getId(), "장보기");
        service.create(list.getId(), "청소하기");

        var result = service.findByListId(list.getId());

        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("ID로 리마인더를 조회할 수 있다")
    void findById() {
        var created = service.create(list.getId(), "장보기");

        var result = service.findById(created.getId());

        assertThat(result.getTitle()).isEqualTo("장보기");
    }

    @Test
    @DisplayName("존재하지 않는 ID 조회 시 예외가 발생한다")
    void findByIdNotFound() {
        assertThatThrownBy(() -> service.findById(999L))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("리마인더를 생성할 수 있다")
    void create() {
        var result = service.create(list.getId(), "장보기");

        assertThat(result.getId()).isNotNull();
        assertThat(result.getTitle()).isEqualTo("장보기");
        assertThat(result.isCompleted()).isFalse();
        assertThat(result.getList().getId()).isEqualTo(list.getId());
    }

    @Test
    @DisplayName("리마인더 상세 필드를 수정할 수 있다")
    void update() {
        var created = service.create(list.getId(), "장보기");

        var result = service.update(created.getId(), "우유 사기", "서울우유",
                LocalDate.of(2026, 3, 10), LocalTime.of(14, 0), Priority.HIGH);

        assertThat(result.getTitle()).isEqualTo("우유 사기");
        assertThat(result.getMemo()).isEqualTo("서울우유");
        assertThat(result.getDueDate()).isEqualTo(LocalDate.of(2026, 3, 10));
        assertThat(result.getDueTime()).isEqualTo(LocalTime.of(14, 0));
        assertThat(result.getPriority()).isEqualTo(Priority.HIGH);
    }

    @Test
    @DisplayName("완료 상태를 토글할 수 있다")
    void toggle() {
        var created = service.create(list.getId(), "장보기");

        var toggled = service.toggle(created.getId());
        assertThat(toggled.isCompleted()).isTrue();

        var toggledBack = service.toggle(created.getId());
        assertThat(toggledBack.isCompleted()).isFalse();
    }

    @Test
    @DisplayName("리마인더를 삭제할 수 있다")
    void delete() {
        var created = service.create(list.getId(), "장보기");

        service.delete(created.getId());

        assertThat(reminderRepository.findById(created.getId())).isEmpty();
    }

    @Test
    @DisplayName("리마인더 조회 시 displayOrder 순으로 정렬된다")
    void findByListIdOrderByDisplayOrder() {
        var r1 = service.create(list.getId(), "세 번째");
        var r2 = service.create(list.getId(), "첫 번째");
        var r3 = service.create(list.getId(), "두 번째");
        r1.updateDisplayOrder(3);
        r2.updateDisplayOrder(1);
        r3.updateDisplayOrder(2);

        var result = service.findByListId(list.getId());

        assertThat(result.get(0).getTitle()).isEqualTo("첫 번째");
        assertThat(result.get(1).getTitle()).isEqualTo("두 번째");
        assertThat(result.get(2).getTitle()).isEqualTo("세 번째");
    }
}