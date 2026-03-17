package com.ai.reminder.service;

import com.ai.reminder.domain.ReminderList;
import com.ai.reminder.repository.ReminderListRepository;
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
public class DefaultReminderListServiceTest {

    @Autowired
    private DefaultReminderListService reminderListService;

    @Autowired
    private ReminderListRepository reminderListRepository;

    private ReminderList defaultList;
    private ReminderList userList;

    @BeforeEach
    void setUp() {
        reminderListRepository.deleteAll();

        defaultList = reminderListRepository.save(ReminderList.builder()
                .name("미리 알림")
                .color("#007AFF")
                .isDefault(true)
                .build());

        userList = reminderListRepository.save(ReminderList.builder()
                .name("업무")
                .color("#FF3830")
                .isDefault(false)
                .build());
    }

    @Test
    @DisplayName("전체 목록을 조회할 수 있다")
    void findAll() {
        var result = reminderListService.findAll();

        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("ID로 목록을 조회할 수 있다")
    void findById() {
        var result = reminderListService.findById(defaultList.getId());
        assertThat(result).isEqualTo(defaultList);
        assertThat(result.getName()).isEqualTo("미리 알림");
    }

    @Test
    @DisplayName("존재하지 않는 ID 조회 시 예외가 발생한다")
    void findByIdNotFound() {
        assertThatThrownBy(() -> reminderListService.findById(999L))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("새 목록을 생성할 수 있다")
    void create() {
        var result = reminderListService.save("개인", "#34C759");

        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo("개인");
        assertThat(result.getColor()).isEqualTo("#34C759");
        assertThat(result.isDefault()).isFalse();
        assertThat(reminderListRepository.findAll()).hasSize(3);
    }

    @Test
    @DisplayName("목록 이름과 색상을 수정할 수 있다")
     void update() {
        var result = reminderListService.update(userList.getId(), "개인", "#34C759");

       assertThat(result.getName()).isEqualTo("개인");
       assertThat(result.getColor()).isEqualTo("#34C759");
    }

    @Test
    @DisplayName("사용자 목록을 삭제할 수 있다")
    void delete() {
        reminderListService.delete(userList.getId());

        assertThat(reminderListRepository.findById(userList.getId())).isEmpty();
        assertThat(reminderListRepository.findAll()).hasSize(1);
    }

    @Test
    @DisplayName("기본 목록은 삭제할 수 없다")
    void deleteDefaultListThrows() {
        assertThatThrownBy(() -> reminderListService.delete(defaultList.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("");
    }
}