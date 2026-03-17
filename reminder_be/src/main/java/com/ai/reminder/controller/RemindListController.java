package com.ai.reminder.controller;

import com.ai.reminder.dto.ReminderListRequest;
import com.ai.reminder.dto.ReminderListResponse;
import com.ai.reminder.service.ports.in.ReminderListService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lists")
@RequiredArgsConstructor
public class RemindListController {

    private final ReminderListService reminderListService;

    @GetMapping()
    public List<ReminderListResponse> findAll() {
        return reminderListService.findAll().stream()
                .map(ReminderListResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public ReminderListResponse findById(@PathVariable Long id) {
        return ReminderListResponse.from(reminderListService.findById(id));
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public ReminderListResponse create(@Valid @RequestBody ReminderListRequest request) {
        return ReminderListResponse.from(reminderListService.save(request.name(), request.color()));
    }

    @PutMapping("/{id}")
    public ReminderListResponse update(@PathVariable Long id, @RequestBody ReminderListRequest request) {
        return ReminderListResponse.from(reminderListService.update(id, request.name(), request.color()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        reminderListService.delete(id);
    }
}
