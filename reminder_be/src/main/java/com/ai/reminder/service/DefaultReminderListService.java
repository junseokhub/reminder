package com.ai.reminder.service;

import com.ai.reminder.domain.ReminderList;
import com.ai.reminder.repository.ReminderListRepository;
import com.ai.reminder.service.ports.in.ReminderListService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DefaultReminderListService implements ReminderListService {

    private final ReminderListRepository repository;

    @Override
    public List<ReminderList> findAll() {
        return repository.findAll();
    }

    @Override
    public ReminderList findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("목록을 찾을 수 없습니다. id=" + id));
    }

    @Override
    @Transactional
    public ReminderList create(String name, String color) {
        ReminderList list = ReminderList.builder()
                .name(name)
                .color(color)
                .isDefault(false)
                .build();
        return repository.save(list);
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
            throw new IllegalStateException("기본 목록은 삭제할 수 없습니다.");
        }
        repository.delete(list);
    }
}
