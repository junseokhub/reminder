package com.ai.reminder.controller;

import com.ai.reminder.domain.ReminderList;
import com.ai.reminder.dto.ReminderListRequest;
import com.ai.reminder.repository.ReminderListRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import static org.hamcrest.Matchers.hasSize;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class RemindListControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
    @DisplayName("GET /api/lists - 전체 목록을 조회한다")
    void findAll() throws Exception {
        mockMvc.perform(get("/api/lists"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("미리 알림"))
                .andExpect(jsonPath("$[1].name").value("업무"));
    }

    @Test
    @DisplayName("GET /api/lists/{id} - ID로 목록을 조회한다")
    void findById() throws Exception {
        mockMvc.perform(get("/api/lists/{id}", defaultList.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("미리 알림"))
                .andExpect(jsonPath("$.color").value("#007AFF"))
                .andExpect(jsonPath("$.isDefault").value(true));
    }

    @Test
    @DisplayName("GET /api/lists/{id} - 존재하지 않는 ID 조회 시 404를 반환한다")
    void findByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/lists/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/lists - 새 목록을 생성한다")
    void create() throws Exception {
        var request = new ReminderListRequest("개인", "#34C759");

        mockMvc.perform(post("/api/lists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("개인"))
                .andExpect(jsonPath("$.color").value("#34C759"))
                .andExpect(jsonPath("$.isDefault").value(false));
    }

    @Test
    @DisplayName("PUT /api/lists/{id} - 목록을 수정한다")
    void update() throws Exception {
        var request = new ReminderListRequest("개인", "#34C759");

        mockMvc.perform(put("/api/lists/{id}", userList.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("개인"))
                .andExpect(jsonPath("$.color").value("#34C759"));
    }

    @Test
    @DisplayName("DELETE /api/lists/{id} - 사용자 목록을 삭제한다")
    void deleteList() throws Exception {
        mockMvc.perform(delete("/api/lists/{id}", userList.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/lists/{id} - 기본 목록 삭제 시 400을 반환한다")
    void deleteDefaultList() throws Exception {
        mockMvc.perform(delete("/api/lists/{id}", defaultList.getId()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/lists - name이 빈 문자열이면 400을 반환한다")
    void createWithBlankName() throws Exception {
        var request = new ReminderListRequest("  ", "#007AFF");

        mockMvc.perform(post("/api/lists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/lists - color가 빈 문자열이면 400을 반환한다")
    void createWithBlankColor() throws Exception {
        var request = new ReminderListRequest("테스트", "  ");

        mockMvc.perform(post("/api/lists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/lists/{id} - 존재하지 않는 ID 조회 시 표준 에러 형식을 반환한다")
    void findByIdNotFoundErrorFormat() throws Exception {
        mockMvc.perform(get("/api/lists/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").isString())
                .andExpect(jsonPath("$.timestamp").isString());
    }
}