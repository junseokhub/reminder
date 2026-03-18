package com.ai.reminder.controller;

import com.ai.reminder.dto.ReminderListRequest;
import com.ai.reminder.dto.ReminderListResponse;
import com.ai.reminder.repository.ReminderRepository;
import com.ai.reminder.service.ports.in.ReminderListService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lists")
@RequiredArgsConstructor
public class ReminderListController {

    private final ReminderListService reminderListService;
    private final ReminderRepository reminderRepository;

    @GetMapping
    public List<ReminderListResponse> findAll() {
        return reminderListService.findAll().stream()
                .map(list -> ReminderListResponse.from(list,
                        reminderRepository.countByListIdAndCompletedFalse(list.getId())))
                .toList();
    }

    @GetMapping("/{id}")
    public ReminderListResponse findById(@PathVariable Long id) {
        var list = reminderListService.findById(id);
        return ReminderListResponse.from(list,
                reminderRepository.countByListIdAndCompletedFalse(list.getId()));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReminderListResponse create(@Valid @RequestBody ReminderListRequest request) {
        var list = reminderListService.save(request.name(), request.color());
        return ReminderListResponse.from(list, 0);
    }

    @PutMapping("/{id}")
    public ReminderListResponse update(@PathVariable Long id, @Valid @RequestBody ReminderListRequest request) {
        var list = reminderListService.update(id, request.name(), request.color());
        return ReminderListResponse.from(list,
                reminderRepository.countByListIdAndCompletedFalse(list.getId()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        reminderListService.delete(id);
    }
}