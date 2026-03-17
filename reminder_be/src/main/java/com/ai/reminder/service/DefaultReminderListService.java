package com.ai.reminder.service;

import com.ai.reminder.domain.ReminderList;
import com.ai.reminder.repository.ReminderListRepository;
import com.ai.reminder.service.ports.in.ReminderListService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DefaultReminderListService implements ReminderListService {

    private final ReminderListRepository reminderListRepository;

    @Override
    public List<ReminderList> findAll() { return reminderListRepository.findAll();}

    @Override
    public ReminderList findById(Long id) {
        return reminderListRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(""));
    }

    @Override
    @Transactional
    public ReminderList save(String name, String color) {
        ReminderList list = ReminderList.builder()
                .name(name)
                .color(color)
                .build();
        return reminderListRepository.save(list);
    }

    @Override
    @Transactional
    public ReminderList update(Long id, String name, String color) {
        ReminderList list = findById(id);
        list.update(name, color);
        return list;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        ReminderList list = findById(id);
        if (list.isDefault()) {
            throw new IllegalStateException("");
        }
        reminderListRepository.delete(list);
    }

}
