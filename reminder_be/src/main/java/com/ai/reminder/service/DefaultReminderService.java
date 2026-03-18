package com.ai.reminder.service;

import com.ai.reminder.domain.Priority;
import com.ai.reminder.domain.Reminder;
import com.ai.reminder.domain.ReminderList;
import com.ai.reminder.repository.ReminderRepository;
import org.springframework.transaction.annotation.Transactional;
import com.ai.reminder.service.ports.in.ReminderListService;
import com.ai.reminder.service.ports.in.ReminderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DefaultReminderService implements ReminderService {

    private final ReminderRepository repository;
    private final ReminderListService reminderListService;

    @Override
    public List<Reminder> findByListId(Long listId) {
        return repository.findByListIdOrderByDisplayOrderAscCreatedAtAsc(listId);
    }

    @Override
    public Reminder findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("리마인더를 찾을 수 없습니다. id=" + id));
    }

    @Override
    @Transactional
    public Reminder create(Long listId, String title) {
        ReminderList list = reminderListService.findById(listId);
        Reminder reminder = Reminder.builder()
                .title(title)
                .list(list)
                .build();
        return repository.save(reminder);
    }

    @Override
    @Transactional
    public Reminder update(Long id, String title, String memo, LocalDate dueDate, LocalTime dueTime, Priority priority) {
        Reminder reminder = findById(id);
        reminder.update(title, memo, dueDate, dueTime, priority);
        return reminder;
    }

    @Override
    @Transactional
    public Reminder toggle(Long id) {
        Reminder reminder = findById(id);
        reminder.toggleCompleted();
        return reminder;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Reminder reminder = findById(id);
        repository.delete(reminder);
    }
}