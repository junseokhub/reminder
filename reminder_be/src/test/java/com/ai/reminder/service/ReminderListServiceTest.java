package com.ai.reminder.service;

import com.ai.reminder.domain.Reminder;
import com.ai.reminder.domain.ReminderList;
import com.ai.reminder.repository.ReminderListRepository;
import com.ai.reminder.repository.ReminderRepository;
import com.ai.reminder.service.ports.in.ReminderListService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class ReminderListServiceTest {

    @Autowired
    private ReminderListService service;

    @Autowired
    private ReminderListRepository repository;

    @Autowired
    private ReminderRepository reminderRepository;

    @Autowired
    private EntityManager entityManager;

    private ReminderList defaultList;
    private ReminderList userList;

    @BeforeEach
    void setUp() {
        reminderRepository.deleteAll();
        repository.deleteAll();

        defaultList = repository.save(ReminderList.builder()
                .name("미리 알림")
                .color("#007AFF")
                .isDefault(true)
                .build());

        userList = repository.save(ReminderList.builder()
                .name("업무")
                .color("#FF3B30")
                .isDefault(false)
                .build());
    }

    @Test
    @DisplayName("전체 목록을 조회할 수 있다")
    void findAll() {
        var result = service.findAll();

        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("ID로 목록을 조회할 수 있다")
    void findById() {
        var result = service.findById(defaultList.getId());

        assertThat(result.getName()).isEqualTo("미리 알림");
        assertThat(result.getColor()).isEqualTo("#007AFF");
    }

    @Test
    @DisplayName("존재하지 않는 ID 조회 시 예외가 발생한다")
    void findByIdNotFound() {
        assertThatThrownBy(() -> service.findById(999L))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("새 목록을 생성할 수 있다")
    void create() {
        var result = service.save("개인", "#34C759");

        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo("개인");
        assertThat(result.getColor()).isEqualTo("#34C759");
        assertThat(result.isDefault()).isFalse();
        assertThat(repository.findAll()).hasSize(3);
    }

    @Test
    @DisplayName("목록 이름과 색상을 수정할 수 있다")
    void update() {
        var result = service.update(userList.getId(), "개인", "#34C759");

        assertThat(result.getName()).isEqualTo("개인");
        assertThat(result.getColor()).isEqualTo("#34C759");
    }

    @Test
    @DisplayName("사용자 목록을 삭제할 수 있다")
    void delete() {
        service.delete(userList.getId());

        assertThat(repository.findById(userList.getId())).isEmpty();
        assertThat(repository.findAll()).hasSize(1);
    }

    @Test
    @DisplayName("기본 목록은 삭제할 수 없다")
    void deleteDefaultListThrows() {
        assertThatThrownBy(() -> service.delete(defaultList.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("기본 목록은 삭제할 수 없습니다");
    }

    @Test
    @DisplayName("목록 삭제 시 하위 리마인더도 함께 삭제된다")
    void deleteListCascadesReminders() {
        reminderRepository.save(Reminder.builder().title("할 일 1").list(userList).build());
        reminderRepository.save(Reminder.builder().title("할 일 2").list(userList).build());
        entityManager.flush();
        entityManager.clear();

        service.delete(userList.getId());
        entityManager.flush();

        assertThat(repository.findById(userList.getId())).isEmpty();
        assertThat(reminderRepository.findByListIdOrderByDisplayOrderAscCreatedAtAsc(userList.getId())).isEmpty();
    }
}