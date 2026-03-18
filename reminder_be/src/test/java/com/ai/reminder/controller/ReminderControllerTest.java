package com.ai.reminder.controller;

import com.ai.reminder.domain.Reminder;
import com.ai.reminder.domain.ReminderList;
import com.ai.reminder.dto.ReminderRequest;
import com.ai.reminder.repository.ReminderListRepository;
import com.ai.reminder.repository.ReminderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ReminderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ReminderListRepository listRepository;

    @Autowired
    private ReminderRepository reminderRepository;

    private ReminderList list;
    private Reminder reminder;

    @BeforeEach
    void setUp() {
        reminderRepository.deleteAll();
        listRepository.deleteAll();

        list = listRepository.save(ReminderList.builder()
                .name("미리 알림")
                .color("#007AFF")
                .isDefault(true)
                .build());

        reminder = reminderRepository.save(Reminder.builder()
                .title("장보기")
                .list(list)
                .build());
    }

    @Test
    @DisplayName("GET /api/lists/{listId}/reminders - 목록별 리마인더를 조회한다")
    void findByListId() throws Exception {
        mockMvc.perform(get("/api/lists/{listId}/reminders", list.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("장보기"));
    }

    @Test
    @DisplayName("POST /api/reminders - 리마인더를 생성한다")
    void create() throws Exception {
        var request = new ReminderRequest(list.getId(), "청소하기", null, null, null, null);

        mockMvc.perform(post("/api/reminders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value("청소하기"))
                .andExpect(jsonPath("$.completed").value(false))
                .andExpect(jsonPath("$.listId").value(list.getId()));
    }

    @Test
    @DisplayName("PUT /api/reminders/{id} - 리마인더를 수정한다")
    void update() throws Exception {
        var request = new ReminderRequest(list.getId(), "우유 사기", "서울우유", null, null, "HIGH");

        mockMvc.perform(put("/api/reminders/{id}", reminder.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("우유 사기"))
                .andExpect(jsonPath("$.memo").value("서울우유"))
                .andExpect(jsonPath("$.priority").value("HIGH"));
    }

    @Test
    @DisplayName("PATCH /api/reminders/{id}/toggle - 완료 상태를 토글한다")
    void toggle() throws Exception {
        mockMvc.perform(patch("/api/reminders/{id}/toggle", reminder.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completed").value(true))
                .andExpect(jsonPath("$.completedAt").isNotEmpty());
    }

    @Test
    @DisplayName("DELETE /api/reminders/{id} - 리마인더를 삭제한다")
    void deleteReminder() throws Exception {
        mockMvc.perform(delete("/api/reminders/{id}", reminder.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("POST /api/reminders - title이 빈 문자열이면 400을 반환한다")
    void createWithBlankTitle() throws Exception {
        var request = new ReminderRequest(list.getId(), "  ", null, null, null, null);

        mockMvc.perform(post("/api/reminders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/reminders - listId가 null이면 400을 반환한다")
    void createWithNullListId() throws Exception {
        var request = new ReminderRequest(null, "테스트", null, null, null, null);

        mockMvc.perform(post("/api/reminders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/reminders/{id} - 잘못된 priority 값이면 400을 반환한다")
    void updateWithInvalidPriority() throws Exception {
        var request = new ReminderRequest(list.getId(), "테스트", null, null, null, "INVALID");

        mockMvc.perform(put("/api/reminders/{id}", reminder.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/reminders - 존재하지 않는 listId이면 404를 반환한다")
    void createWithNonExistentListId() throws Exception {
        var request = new ReminderRequest(999L, "테스트", null, null, null, null);

        mockMvc.perform(post("/api/reminders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }
}