package com.ai.reminder.service.ports.in;

import com.ai.reminder.domain.ReminderList;

import java.util.List;

public interface ReminderListService {

    List<ReminderList> findAll();
    ReminderList findById(Long id);
    ReminderList create(String name, String color);
    ReminderList update(Long id, String name, String color);
    void delete(Long id);
}
