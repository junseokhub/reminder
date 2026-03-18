package com.ai.reminder.controller;

import com.ai.reminder.domain.Priority;
import com.ai.reminder.dto.ReminderRequest;
import com.ai.reminder.dto.ReminderResponse;
import com.ai.reminder.service.ports.in.ReminderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReminderController {

    private final ReminderService service;

    @GetMapping("/api/lists/{listId}/reminders")
    public List<ReminderResponse> findByListId(@PathVariable Long listId) {
        return service.findByListId(listId).stream()
                .map(ReminderResponse::from)
                .toList();
    }

    @PostMapping("/api/reminders")
    @ResponseStatus(HttpStatus.CREATED)
    public ReminderResponse create(@Valid @RequestBody ReminderRequest request) {
        return ReminderResponse.from(service.create(request.listId(), request.title()));
    }

    @PutMapping("/api/reminders/{id}")
    public ReminderResponse update(@PathVariable Long id, @Valid @RequestBody ReminderRequest request) {
        Priority priority = request.priority() != null ? Priority.valueOf(request.priority()) : Priority.NONE;
        return ReminderResponse.from(service.update(id, request.title(), request.memo(), request.dueDate(), request.dueTime(), priority));
    }

    @PatchMapping("/api/reminders/{id}/toggle")
    public ReminderResponse toggle(@PathVariable Long id) {
        return ReminderResponse.from(service.toggle(id));
    }

    @DeleteMapping("/api/reminders/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}